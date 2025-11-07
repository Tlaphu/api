package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.dto.req.FormJob;
import com.ra.base_spring_boot.dto.req.FormJobResponseDTO;
import com.ra.base_spring_boot.dto.resp.DashboardStats;
import com.ra.base_spring_boot.services.ICompanyAuthService;

import com.ra.base_spring_boot.services.JobCandidateService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/job")
@RequiredArgsConstructor
public class JobController {

    private final JobRepository jobRepository;
    private final ICompanyRepository companyRepository;
    private final ILocationRepository locationRepository;
    private final ICompanyAuthService companyAuthService;
    private final ICandidateRepository candidateRepository;
    private final JobCandidateService jobCandidateService;

    private final ISkillRepository skillRepository;
    private final LevelJobRepository levelJobRepository;
    private final LevelJobRelationRepository levelJobRelationRepository;


    private String buildAutoRequirements(LevelJob levelJob, Set<Skill> skills) {
        StringBuilder sb = new StringBuilder("[");

        if (levelJob != null) {
            sb.append("Cấp độ: ").append(levelJob.getName());
        }

        if (skills != null && !skills.isEmpty()) {
            List<String> skillNames = skills.stream()
                    .map(Skill::getName)
                    .collect(Collectors.toList());
            if (levelJob != null) {
                sb.append(". ");
            }
            sb.append("Kỹ năng: ").append(String.join(", ", skillNames));
        }

        sb.append("]");

        return sb.toString();
    }


    @Scheduled(cron = "0 30 1 * * *")
    @Transactional
    public void autoUpdateExpiredJobs() {
        Date currentDate = new Date();
        String inactiveStatus = "INACTIVE";


        List<Job> jobsToDeactivate = jobRepository.findJobsToExpire(currentDate, inactiveStatus);

        if (jobsToDeactivate.isEmpty()) {
            return;
        }

        for (Job job : jobsToDeactivate) {
            job.setStatus(inactiveStatus);
            job.setUpdated_at(new Date());
        }

        jobRepository.saveAll(jobsToDeactivate);
    }


    @GetMapping("/company/{companyName}")
    public ResponseEntity<?> getJobsByCompany(@PathVariable String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Company name must be provided in the path.");
        }

        Optional<Company> companyOpt = companyRepository.findByName(companyName);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Company not found with name: " + companyName);
        }

        Company company = companyOpt.get();


        List<Job> companyJobs = jobRepository.findByCompanyId(company.getId());

        if (companyJobs.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<FormJobResponseDTO> jobs = companyJobs.stream()
                .map(job -> FormJobResponseDTO.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .salary(job.getSalary())
                        .requirements(job.getRequirements())
                        .desirable(job.getDesirable())
                        .benefits(job.getBenefits())
                        .workTime(job.getWorkTime())
                        .companyName(company.getName())
                        .companyLogo(company.getLogo())
                        .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : null)
                        .created_at(job.getCreated_at())
                        .expire_at(job.getExpire_at())
                        .status(job.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }


    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody FormJob form) {

        Location location = null;
        Long locationId = form.getLocationId();


        if (locationId != null) {
            Optional<Location> locationOpt = locationRepository.findById(locationId);
            if (locationOpt.isPresent()) {
                location = locationOpt.get();
            } else {
                return ResponseEntity.status(404).body("Location not found with ID: " + locationId);
            }
        }


        String companyName = form.getCompanyName();
        if (companyName == null || companyName.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Company name must be provided in the request body.");
        }

        Optional<Company> companyOpt = companyRepository.findByName(companyName);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Company not found with name: " + companyName);
        }

        Company company = companyOpt.get();


        Set<Skill> skills = new HashSet<>();
        if (form.getSkillIds() != null && !form.getSkillIds().isEmpty()) {
            skills = new HashSet<>(skillRepository.findAllById(form.getSkillIds()));
            if (skills.size() != form.getSkillIds().size()) {
                return ResponseEntity.status(404).body("One or more Skills not found.");
            }
        }


        LevelJob levelJob = null;
        if (form.getLevelJobId() != null) {
            Optional<LevelJob> levelJobOpt = levelJobRepository.findById(form.getLevelJobId());
            if (levelJobOpt.isEmpty()) {
                return ResponseEntity.status(404).body("LevelJob not found with ID: " + form.getLevelJobId());
            }
            levelJob = levelJobOpt.get();
        }


        String autoRequirements = buildAutoRequirements(levelJob, skills);


        Job job = Job.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .salary(form.getSalary())

                .requirements(autoRequirements)

                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())
                .location(location)
                .company(company)
                .created_at(new Date())
                .expire_at(form.getExpire_at())
                .status(form.getStatus() != null ? form.getStatus() : "ACTIVE")
                .skills(skills)
                .build();

        Job savedJob = jobRepository.save(job);


        if (levelJob != null) {
            LevelJobRelation relation = LevelJobRelation.builder()
                    .job(savedJob)
                    .levelJob(levelJob)
                    .build();
            levelJobRelationRepository.save(relation);
        }


        FormJobResponseDTO response = FormJobResponseDTO.builder()
                .id(savedJob.getId())
                .title(savedJob.getTitle())
                .description(savedJob.getDescription())
                .salary(savedJob.getSalary())
                .requirements(savedJob.getRequirements())
                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())

                .companyName(company.getName())
                .companyLogo(company.getLogo())

                .locationId(location != null ? location.getId() : null)
                .created_at(savedJob.getCreated_at())
                .expire_at(savedJob.getExpire_at())
                .status(savedJob.getStatus())
                .build();

        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<?> getAll() {

        List<Job> activeJobs = jobRepository.findByStatus("ACTIVE");

        List<FormJobResponseDTO> jobs = activeJobs.stream()
                .map(job -> FormJobResponseDTO.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .salary(job.getSalary())
                        .requirements(job.getRequirements())
                        .desirable(job.getDesirable())
                        .benefits(job.getBenefits())
                        .workTime(job.getWorkTime())
                        .companyName(job.getCompany() != null ? job.getCompany().getName() : "N/A")
                        .companyLogo(job.getCompany() != null ? job.getCompany().getLogo() : "N/A")
                        .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
                        .created_at(job.getCreated_at())
                        .expire_at(job.getExpire_at())
                        .status(job.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }


    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<?> getAllJobsForManagement() {

        List<Job> allJobs = jobRepository.findAll();

        if (allJobs.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<FormJobResponseDTO> jobs = allJobs.stream()
                .map(job -> FormJobResponseDTO.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .salary(job.getSalary())
                        .requirements(job.getRequirements())
                        .desirable(job.getDesirable())
                        .benefits(job.getBenefits())
                        .workTime(job.getWorkTime())
                        .companyName(job.getCompany() != null ? job.getCompany().getName() : "N/A")
                        .companyLogo(job.getCompany() != null ? job.getCompany().getLogo() : "N/A")
                        .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
                        .created_at(job.getCreated_at())
                        .expire_at(job.getExpire_at())
                        .status(job.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        Job job = jobOpt.get();

        // Lấy LevelJob Name
        String levelJobName = job.getLevelJobRelations() != null && !job.getLevelJobRelations().isEmpty()
                ? job.getLevelJobRelations().get(0).getLevelJob().getName()
                : null;

        // Lấy danh sách tên Skills
        List<String> skillNames = job.getSkills().stream()
                .map(Skill::getName)
                .collect(Collectors.toList());


        FormJobResponseDTO dto = FormJobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salary(job.getSalary())
                .requirements(job.getRequirements())
                .desirable(job.getDesirable())
                .benefits(job.getBenefits())
                .workTime(job.getWorkTime())
                .companyName(job.getCompany() != null ? job.getCompany().getName() : null)
                .companyLogo(job.getCompany() != null ? job.getCompany().getLogo() : null)
                .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                .locationName(job.getLocation() != null ? job.getLocation().getName() : null)
                .created_at(job.getCreated_at())
                .expire_at(job.getExpire_at())
                .status(job.getStatus())


                .build();

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FormJob form) {

        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        Job job = jobOpt.get();

        Location location = null;
        Long locationId = form.getLocationId();
        if (locationId != null) {
            Optional<Location> locationOpt = locationRepository.findById(locationId);
            if (locationOpt.isPresent()) {
                location = locationOpt.get();
            } else {
                return ResponseEntity.status(404).body("Location not found with ID: " + locationId);
            }
        } else {

            location = job.getLocation();
        }

        String companyName = form.getCompanyName();
        if (companyName == null || companyName.trim().isEmpty()) {
            return ResponseEntity.status(400).body("Company name must be provided in the request body.");
        }

        Optional<Company> companyOpt = companyRepository.findByName(companyName);
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Company not found with name: " + companyName);
        }

        Company company = companyOpt.get();


        Set<Skill> skills = new HashSet<>();
        if (form.getSkillIds() != null) {
            skills = new HashSet<>(skillRepository.findAllById(form.getSkillIds()));
            if (skills.size() != form.getSkillIds().size()) {
                return ResponseEntity.status(404).body("One or more Skills not found.");
            }
        }
        job.getSkills().clear();
        job.getSkills().addAll(skills);


        LevelJob newLevelJob = null;
        if (form.getLevelJobId() != null) {
            Optional<LevelJob> levelJobOpt = levelJobRepository.findById(form.getLevelJobId());
            if (levelJobOpt.isEmpty()) {
                return ResponseEntity.status(404).body("LevelJob not found with ID: " + form.getLevelJobId());
            }
            newLevelJob = levelJobOpt.get();


            if (job.getLevelJobRelations() != null) {
                levelJobRelationRepository.deleteAll(job.getLevelJobRelations());
                job.getLevelJobRelations().clear();
            }


            LevelJobRelation newRelation = LevelJobRelation.builder()
                    .job(job)
                    .levelJob(newLevelJob)
                    .build();
            levelJobRelationRepository.save(newRelation);
        }


        String autoRequirements = buildAutoRequirements(newLevelJob, skills);


        job.setTitle(form.getTitle());
        job.setDescription(form.getDescription());
        job.setSalary(form.getSalary());


        job.setRequirements(autoRequirements);

        job.setDesirable(form.getDesirable());
        job.setBenefits(form.getBenefits());
        job.setWorkTime(form.getWorkTime());
        job.setLocation(location);
        job.setCompany(company);


        if (form.getStatus() != null && !form.getStatus().trim().isEmpty()) {
            job.setStatus(form.getStatus());
        }

        job.setExpire_at(form.getExpire_at());
        job.setUpdated_at(new Date());

        jobRepository.save(job);

        FormJobResponseDTO response = FormJobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salary(job.getSalary())
                .requirements(job.getRequirements()) // Trả về chuỗi tự động
                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())
                .companyName(company.getName())
                .companyLogo(company.getLogo())
                .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                .locationName(job.getLocation() != null ? job.getLocation().getName() : null)
                .created_at(job.getCreated_at())
                .expire_at(job.getExpire_at())
                .status(job.getStatus())
                .build();

        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        jobRepository.deleteById(id);
        return ResponseEntity.ok("Deleted Job successfully with id: " + id);
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedJobs() {
        List<Job> allJobs = jobRepository.findByStatus("ACTIVE");

        List<FormJobResponseDTO> jobs = allJobs.stream()
                .sorted((a, b) -> Double.compare(
                        b.getSalary() != null ? b.getSalary() : 0,
                        a.getSalary() != null ? a.getSalary() : 0
                ))
                .limit(10)
                .map(job -> FormJobResponseDTO.builder()
                        .id(job.getId())
                        .title(job.getTitle())
                        .description(job.getDescription())
                        .salary(job.getSalary())
                        .requirements(job.getRequirements())
                        .desirable(job.getDesirable())
                        .benefits(job.getBenefits())
                        .workTime(job.getWorkTime())
                        .companyName(job.getCompany() != null ? job.getCompany().getName() : "N/A")
                        .companyLogo(job.getCompany() != null ? job.getCompany().getLogo() : "N/A")
                        .locationId(job.getLocation() != null ? job.getLocation().getId() : null)
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : null)
                        .created_at(job.getCreated_at())
                        .expire_at(job.getExpire_at())
                        .status(job.getStatus())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/stats")
    public ResponseEntity<DashboardStats> getDashboardStats() {

        Long liveJobsCount = jobRepository.countByStatus("ACTIVE");

        Long companyCount = companyRepository.count();

        Long candidateCount = candidateRepository.count();


        Instant tenDaysAgo = Instant.now().minus(10, ChronoUnit.DAYS);
        Date startDate = Date.from(tenDaysAgo);


        Long newJobsCount = jobRepository.countNewActiveJobs("ACTIVE",startDate);

        DashboardStats stats = DashboardStats.builder()
                .liveJobs(liveJobsCount)
                .companies(companyCount)
                .candidates(candidateCount)
                .newJobs(newJobsCount)
                .build();

        return ResponseEntity.ok(stats);
    }
    @GetMapping("/{jobId}/suitable-candidates")
    public ResponseEntity<?> getSuitableCandidates(@PathVariable Long jobId) {
        List<CandidateResponse> responses = jobCandidateService.getSuitableCandidatesForCompanyJob(jobId);
        return ResponseEntity.ok(responses);
    }
}