package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormJobCandidate;
import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;
import com.ra.base_spring_boot.services.JobCandidateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/job-candidates")
public class JobCandidateController {

    private final JobCandidateService jobCandidateService;

    @Autowired
    public JobCandidateController(JobCandidateService jobCandidateService) {
        this.jobCandidateService = jobCandidateService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
    public ResponseEntity<JobCandidateResponse> createJobCandidate(@Valid @RequestBody FormJobCandidate form) {
        JobCandidateResponse response = jobCandidateService.create(form);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public List<JobCandidateResponse> getAllJobCandidates() {
        return jobCandidateService.findAll();
    }

    @GetMapping("/{id}")
   @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobCandidateResponse> getJobCandidateById(@PathVariable Long id) {
        return jobCandidateService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public List<JobCandidateResponse> getCandidatesByJobId(@PathVariable Long jobId) {
        return jobCandidateService.findByJobId(jobId);
    }

    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAnyAuthority('ROLE_COMPANY', 'ROLE_ADMIN') or (hasAuthority('ROLE_CANDIDATE') and #candidateId == authentication.principal.id)") 
    public List<JobCandidateResponse> getApplicationsByCandidateId(@PathVariable Long candidateId) {
        return jobCandidateService.findByCandidateId(candidateId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobCandidateResponse> updateJobCandidate(@PathVariable Long id,
                                                                 @Valid @RequestBody FormJobCandidate form) {
        try {
            JobCandidateResponse updatedJobCandidate = jobCandidateService.update(id, form);
            return ResponseEntity.ok(updatedJobCandidate);
        } catch (RuntimeException e) {

            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteJobCandidate(@PathVariable Long id) {
        try {
            jobCandidateService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}