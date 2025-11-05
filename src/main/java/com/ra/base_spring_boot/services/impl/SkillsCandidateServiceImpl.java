package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormSkillCandidate;
import com.ra.base_spring_boot.dto.resp.SkillsCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ISkillsCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SkillsCandidateServiceImpl implements ISkillsCandidateService {

    private final ISkillsCandidateRepository skillsRepo;
    private final LevelJobRepository levelJobRepository;
    private final SkillRepository skillRepository;
    private final JwtProvider jwtProvider;

    @Override
    public List<SkillsCandidateResponse> getMySkills() {
        Candidate current = jwtProvider.getCurrentCandidate();

        return skillsRepo.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SkillsCandidateResponse createSkill(FormSkillCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();


        Skill skill = skillRepository.findById(req.getSkillId())
                .orElseThrow(() -> new HttpBadRequest("Skill not found with ID: " + req.getSkillId()));


        LevelJob levelJob = null;
        if (req.getLevelJobId() != null) {
            levelJob = levelJobRepository.findById(req.getLevelJobId())
                    .orElseThrow(() -> new HttpBadRequest("LevelJob not found with ID: " + req.getLevelJobId()));
        }

        SkillsCandidate newSkill = SkillsCandidate.builder()
                .candidate(current)
                .skill(skill)
                .levelJob(levelJob)
                .createdAt(new Date())
                .updatedAt(new Date())
                .build();

        return toResponse(skillsRepo.save(newSkill));
    }

    @Override
    public SkillsCandidateResponse updateSkill(Long id, FormSkillCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        SkillsCandidate existing = skillsRepo.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Skill not found with ID: " + id));

        if (!existing.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own skill");
        }


        if (req.getSkillId() != null) {
            Skill skill = skillRepository.findById(req.getSkillId())
                    .orElseThrow(() -> new HttpBadRequest("Skill not found with ID: " + req.getSkillId()));
            existing.setSkill(skill);
        }


        if (req.getLevelJobId() != null) {
            LevelJob levelJob = levelJobRepository.findById(req.getLevelJobId())
                    .orElseThrow(() -> new HttpBadRequest("LevelJob not found with ID: " + req.getLevelJobId()));
            existing.setLevelJob(levelJob);
        }

        existing.setUpdatedAt(new Date());

        return toResponse(skillsRepo.save(existing));
    }

    @Override
    public void deleteSkill(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();

        SkillsCandidate skill = skillsRepo.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Skill not found with ID: " + id));

        if (!skill.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own skill");
        }

        skillsRepo.delete(skill);
    }

    private SkillsCandidateResponse toResponse(SkillsCandidate skill) {
        return SkillsCandidateResponse.builder()
                .id(skill.getId())
                .skillName(skill.getSkill() != null ? skill.getSkill().getName() : null)
                .levelJobName(skill.getLevelJob() != null ? skill.getLevelJob().getName() : null)
                .createdAt(skill.getCreatedAt())
                .updatedAt(skill.getUpdatedAt())
                .build();
    }
}