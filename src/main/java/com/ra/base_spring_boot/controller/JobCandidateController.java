package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormJobCandidate;
import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;
import com.ra.base_spring_boot.services.JobCandidateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.AccessDeniedException; // ✨ IMPORT MỚI ✨
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/job-candidates")
public class JobCandidateController {

    private final JobCandidateService jobCandidateService;

    @Autowired
    public JobCandidateController(JobCandidateService jobCandidateService) {
        this.jobCandidateService = jobCandidateService;
    }

    // --- CREATE ---
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
    public ResponseEntity<JobCandidateResponse> createJobCandidate(@Valid @RequestBody FormJobCandidate form) {
        JobCandidateResponse response = jobCandidateService.create(form);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // --- READ ALL ---
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<JobCandidateResponse>> getAllJobCandidates() {
        List<JobCandidateResponse> responses = jobCandidateService.findAll();
        return ResponseEntity.ok(responses);
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
    public ResponseEntity<List<JobCandidateResponse>> getCandidatesByJobId(@PathVariable Long jobId) {
        List<JobCandidateResponse> responses = jobCandidateService.findByJobId(jobId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/candidate/{candidateId}")
    @PreAuthorize("hasAuthority('ROLE_CANDIDATE') and #candidateId == authentication.principal.id")
    public ResponseEntity<List<JobCandidateResponse>> getApplicationsByCandidateId(@PathVariable Long candidateId) {
        List<JobCandidateResponse> responses = jobCandidateService.findByCandidateId(candidateId);
        return ResponseEntity.ok(responses);
    }

    // --- UPDATE CHUNG ---
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobCandidateResponse> updateJobCandidate(@PathVariable Long id,
                                                                   @Valid @RequestBody FormJobCandidate form) {
        try {
            JobCandidateResponse updatedJobCandidate = jobCandidateService.update(id, form);
            return ResponseEntity.ok(updatedJobCandidate);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    // --- CẬP NHẬT TRẠNG THÁI ACCEPT/REJECT ---
    @PatchMapping("/accept-reject/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<JobCandidateResponse> setAcceptanceStatus(
            @PathVariable Long id,
            @RequestParam Boolean isAccepted) {
        try {

            JobCandidateResponse updatedJobCandidate = jobCandidateService.setAcceptanceStatus(id, isAccepted);
            return ResponseEntity.ok(updatedJobCandidate);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        } catch (AccessDeniedException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteJobCandidate(@PathVariable Long id) {
        try {
            jobCandidateService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}