package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.repository.ILocationRepository;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.dto.req.FormJob;
import com.ra.base_spring_boot.dto.req.FormJobResponseDTO;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*; 
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/job")
@CrossOrigin(origins = "*")
public class JobController {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private ICompanyRepository companyRepository;

    @Autowired
    private ILocationRepository locationRepository;

    @Autowired
    private ICompanyAuthService companyAuthService;

    // --- 1. POST: CREATE (Đã sửa lỗi) ---
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<?> create(@RequestBody FormJob form) {
        
        // ❗ Đã sửa: Lấy ID kiểu Long trực tiếp từ Form
        Long locationId = form.getLocationId();
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Location not found");
        }

        // ❗ Đã sửa: Lấy ID kiểu Long trực tiếp từ Form
        Long companyId = form.getCompanyId();
        Optional<Company> companyOpt = companyRepository.findById(companyId); 
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Company not found");
        }

        Location location = locationOpt.get();
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
                .build();

        jobRepository.save(job);

        FormJobResponseDTO response = FormJobResponseDTO.builder()
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
                .locationName(location.getName())
                .build();

        return ResponseEntity.status(201).body(response);
    }

    
    // --- 2. GET: ALL ---
    @GetMapping
    public ResponseEntity<?> getAll() {
        List<FormJobResponseDTO> jobs = jobRepository.findAll().stream()
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
                        .companyLogo(job.getCompany()!= null ? job.getCompany().getLogo() : "N/A")
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }

    // --- 3. GET: BY ID ---
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
                .build();

        return ResponseEntity.ok(dto);
    }
    
    // --- 4. PUT: UPDATE (Đã sửa lỗi) ---
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody FormJob form) {
        
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        Job job = jobOpt.get();

        // ❗ Đã sửa: Lấy ID kiểu Long trực tiếp từ Form
        Long locationId = form.getLocationId();
        if (locationId == null) {
            return ResponseEntity.status(400).body("Location ID must be provided in the request body.");
        }
        Optional<Location> locationOpt = locationRepository.findById(locationId);
        if (locationOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Location not found");
        }

        // ❗ Đã sửa: Lấy ID kiểu Long trực tiếp từ Form
        Long companyId = form.getCompanyId();
        if (companyId == null) {
            return ResponseEntity.status(400).body("Company ID must be provided in the request body.");
        }
        Optional<Company> companyOpt = companyRepository.findById(companyId); 
        if (companyOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Company not found");
        }

        
        Location location = locationOpt.get();
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
        job.setExpire_at(form.getExpire_at());
        job.setUpdated_at(new Date());

        jobRepository.save(job);

        
        FormJobResponseDTO response = FormJobResponseDTO.builder()
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
                .locationName(location.getName())
                .build();

        return ResponseEntity.ok(response);
    }
    
    // --- 5. DELETE ---
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Job> jobOpt = jobRepository.findById(id);
        if (jobOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Job not found");
        }

        jobRepository.deleteById(id);
        return ResponseEntity.ok("Deleted Job successfully with id: " + id);
    }

    // --- 6. GET: FEATURED ---
    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedJobs() {
        List<FormJobResponseDTO> jobs = jobRepository.findAll().stream()
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
                        .companyLogo(job.getCompany()!= null ? job.getCompany().getLogo() : "N/A")
                        .locationName(job.getLocation() != null ? job.getLocation().getName() : "N/A")
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(jobs);
    }
}