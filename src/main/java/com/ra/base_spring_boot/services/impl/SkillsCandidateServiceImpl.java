package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormSkillCandidate;
import com.ra.base_spring_boot.dto.resp.SkillsCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.model.SkillsCandidate;
import com.ra.base_spring_boot.repository.LevelJobRepository; // Đã sửa từ ILevelJobRepository sang LevelJobRepository
import com.ra.base_spring_boot.repository.ISkillsCandidateRepository;
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
    private final JwtProvider jwtProvider;
    private final LevelJobRepository levelJobRepository;

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

        // FIX: Tìm LevelJob object từ ID trong request DTO
        LevelJob levelJob = null;
        if (req.getLevelJobId() != null) { // Giả định FormSkillCandidate có getLevelJobId() trả về Long
            levelJob = levelJobRepository.findById(req.getLevelJobId())
                    .orElseThrow(() -> new HttpBadRequest("LevelJob not found with ID: " + req.getLevelJobId()));
        }

        SkillsCandidate skill = SkillsCandidate.builder()
                .candidate(current)
                .name(req.getName())
                .levelJob(levelJob)
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        return toResponse(skillsRepo.save(skill));
    }

    @Override
    public SkillsCandidateResponse updateSkill(Long id, FormSkillCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        SkillsCandidate skill = skillsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own skill");
        }

        // FIX: Tìm LevelJob object từ ID trong request DTO
        LevelJob levelJob = null;
        if (req.getLevelJobId() != null) { // Giả định FormSkillCandidate có getLevelJobId() trả về Long
            levelJob = levelJobRepository.findById(req.getLevelJobId())
                    .orElseThrow(() -> new HttpBadRequest("LevelJob not found with ID: " + req.getLevelJobId()));
        }

        skill.setName(req.getName());
        skill.setLevelJob(levelJob);
        skill.setUpdated_at(new Date());

        return toResponse(skillsRepo.save(skill));
    }

    @Override
    public void deleteSkill(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();

        SkillsCandidate skill = skillsRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        if (!skill.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own skill");
        }

        skillsRepo.delete(skill);
    }

    private SkillsCandidateResponse toResponse(SkillsCandidate skill) {
        return SkillsCandidateResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                // FIX: Lấy ID từ LevelJob object (sử dụng .toString() vì Response DTO likely cần String)
                .level_job_id(skill.getLevelJob() != null ? skill.getLevelJob().getId().toString() : null)
                .created_at(skill.getCreated_at())
                .updated_at(skill.getUpdated_at())
                .build();
    }
}
