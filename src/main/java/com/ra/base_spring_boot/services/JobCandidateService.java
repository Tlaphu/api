package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormJobCandidate;
import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;

import java.util.List;
import java.util.Optional;

public interface JobCandidateService {
    
    JobCandidateResponse create(FormJobCandidate form);
    JobCandidateResponse update(Long id, FormJobCandidate form);
    Optional<JobCandidateResponse> findById(Long id);
    List<JobCandidateResponse> findAll();
    void delete(Long id);

    List<JobCandidateResponse> findByJobId(Long jobId);
    List<JobCandidateResponse> findByCandidateId(Long candidateId);

    List<CandidateResponse> getSuitableCandidatesForCompanyJob(Long jobId);
    void deleteByJobId(Long jobId);
    JobCandidateResponse setAcceptanceStatus(Long id, Boolean isAccepted);
}
