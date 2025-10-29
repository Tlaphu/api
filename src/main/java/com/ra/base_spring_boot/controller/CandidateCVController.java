package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormCandidateCV;
import com.ra.base_spring_boot.dto.resp.CandidateCVResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.CandidateCV;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ICandidateCVService;
import com.ra.base_spring_boot.services.impl.CandidateCVServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/candidate/cv") 
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
public class CandidateCVController {

    private final ICandidateCVService candidateCVService;
    private final CandidateCVServiceImpl candidateCVServiceImpl;

    private Long getAuthenticatedCandidateId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof MyUserDetails)) {
            throw new HttpBadRequest("Unauthorized access."); 
        }
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        return userDetails.getCandidate().getId(); 
    }
    
    @PostMapping
    public ResponseEntity<CandidateCVResponse> createNewCV(@RequestBody FormCandidateCV cvForm) {
        Long candidateId = getAuthenticatedCandidateId();
        CandidateCV newCV = candidateCVService.createNewCV(cvForm, candidateId);
        
        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(newCV);
        
        return ResponseEntity.ok(cvResponse);
    }
    
    @GetMapping
    public ResponseEntity<List<CandidateCVResponse>> getAllCandidateCVs() {
        Long candidateId = getAuthenticatedCandidateId();
        List<CandidateCV> cvList = candidateCVService.getAllCVsByCandidate(candidateId);
        
        List<CandidateCVResponse> responseList = cvList.stream()
            .map(candidateCVServiceImpl::mapToResponse)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<CandidateCVResponse> getCVById(@PathVariable Long cvId) {
        Long candidateId = getAuthenticatedCandidateId();
        CandidateCV cvEntity = candidateCVService.getCVById(cvId, candidateId);
        
        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(cvEntity);
        
        return ResponseEntity.ok(cvResponse);
    }
    
    @PutMapping("/{cvId}")
    public ResponseEntity<CandidateCVResponse> updateCV(@PathVariable Long cvId, @RequestBody FormCandidateCV cvForm) {
        Long candidateId = getAuthenticatedCandidateId();
        CandidateCV updatedCandidate = candidateCVService.updateCV(cvId, cvForm, candidateId);
        
        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(updatedCandidate);
        
        return ResponseEntity.ok(cvResponse); 
    }
    
    @DeleteMapping("/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable Long cvId) {
        Long candidateId = getAuthenticatedCandidateId();
        candidateCVService.deleteCV(cvId, candidateId);
        
        return ResponseEntity.noContent().build();
    }
}