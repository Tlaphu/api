package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FromExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.ExperienceCandidateResponse;

import java.util.List;

public interface IExperienceCandidateService {
    List<ExperienceCandidateResponse> getMyExperiences();
    ExperienceCandidateResponse createExperience(FromExperienceCandidate req);
    ExperienceCandidateResponse updateExperience(Long id, FromExperienceCandidate req);
    void deleteExperience(Long id);
}

