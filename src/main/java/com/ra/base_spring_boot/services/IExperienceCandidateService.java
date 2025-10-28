package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.ExperienceCandidateResponse;

import java.util.List;

public interface IExperienceCandidateService {
    List<ExperienceCandidateResponse> getMyExperiences();
    ExperienceCandidateResponse createExperience(FormExperienceCandidate req);
    ExperienceCandidateResponse updateExperience(Long id, FormExperienceCandidate req);
    void deleteExperience(Long id);
}

