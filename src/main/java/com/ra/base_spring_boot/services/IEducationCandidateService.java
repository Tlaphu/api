package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;

import java.util.List;

public interface IEducationCandidateService {
    List<EducationCandidateResponse> getAllByCandidate(Long candidateId);
    EducationCandidateResponse createByCandidate(String candidateId, FormEducationCandidate request);
    EducationCandidateResponse updateByCandidate(Long id, String candidateId, FormEducationCandidate request);
    void deleteByCandidate(Long id, String candidateId);
}
