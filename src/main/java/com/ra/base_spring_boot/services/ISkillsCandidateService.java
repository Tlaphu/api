package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormSkillCandidate;
import com.ra.base_spring_boot.dto.resp.SkillsCandidateResponse;

import java.util.List;

public interface ISkillsCandidateService {
    List<SkillsCandidateResponse> getMySkills();
    SkillsCandidateResponse createSkill(FormSkillCandidate req);
    SkillsCandidateResponse updateSkill(Long id, FormSkillCandidate req);
    void deleteSkill(Long id);
}
