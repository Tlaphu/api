package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.repository.ILocationRepository;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.dto.req.FormJob;
import com.ra.base_spring_boot.dto.req.FormJobResponseDTO;
import com.ra.base_spring_boot.dto.resp.DashboardStats;
import com.ra.base_spring_boot.services.ICompanyAuthService;

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
                .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
                .created_at(job.getCreated_at())
                .expire_at(job.getExpire_at())
                .status(job.getStatus())
                .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FormJob form) {

        Location location = null;
        String locationName = form.getLocationName();
        if (locationName != null && !locationName.trim().isEmpty()) {
            Optional<Location> locationOpt = locationRepository.findByName(locationName);
            if (locationOpt.isPresent()) {
                location = locationOpt.get();
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

        Job job = Job.builder()
                .title(form.getTitle())
                .description(form.getDescription())
                .salary(form.getSalary())
                .requirements(form.getRequirements())
                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())
                .location(location)
                .company(company)
                .created_at(new Date())
                .expire_at(form.getExpire_at())
                
                .status(form.getStatus() != null ? form.getStatus() : "ACTIVE")
                .build();

        jobRepository.save(job);

        FormJobResponseDTO response = FormJobResponseDTO.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .salary(job.getSalary())
                .requirements(form.getRequirements())
                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())
                .companyName(company.getName())
                .companyLogo(company.getLogo())
                .locationName(location != null ? location.getName() : null)
                .created_at(job.getCreated_at())
                .expire_at(job.getExpire_at())
                .status(job.getStatus())
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
                .locationName(job.getLocation() != null ? job.getLocation().getName() : null)
                .created_at(job.getCreated_at())
                .expire_at(job.getExpire_at())
                .status(job.getStatus())
                .levelJobName(job.getLevelJobRelations() != null && !job.getLevelJobRelations().isEmpty() ? job.getLevelJobRelations().get(0).getLevelJob().getName() : null)
                .build();

        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FormJob form) {

        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        Job job = jobOpt.get();

        Location location = null;
        String locationName = form.getLocationName();
        if (locationName != null && !locationName.trim().isEmpty()) {
            Optional<Location> locationOpt = locationRepository.findByName(locationName);
            if (locationOpt.isPresent()) {
                location = locationOpt.get();
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

        job.setTitle(form.getTitle());
        job.setDescription(form.getDescription());
        job.setSalary(form.getSalary());
        job.setRequirements(form.getRequirements());
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
                .requirements(form.getRequirements())
                .desirable(form.getDesirable())
                .benefits(form.getBenefits())
                .workTime(form.getWorkTime())
                .companyName(company.getName())
                .companyLogo(company.getLogo())
                .locationName(location != null ? location.getName() : null)
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
                .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
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
}