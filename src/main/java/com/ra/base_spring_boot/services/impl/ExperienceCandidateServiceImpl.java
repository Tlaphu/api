package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.ExperienceCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.ExperienceCandidate;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.IExperienceCandidateRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.IExperienceCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperienceCandidateServiceImpl implements IExperienceCandidateService {

    private final IExperienceCandidateRepository experienceRepo;
    private final ICandidateRepository candidateRepo;
    private final JwtProvider jwtProvider;

    @Override
    public List<ExperienceCandidateResponse> getMyExperiences() {
        Candidate current = jwtProvider.getCurrentCandidate();

        return experienceRepo.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ExperienceCandidateResponse createExperience(FormExperienceCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ExperienceCandidate exp = ExperienceCandidate.builder()
                .candidate(current)
                .candidateCV(null)
                .position(req.getPosition())
                .company(req.getCompany())
                .started_at(req.getStarted_at())
                .end_at(req.getEnd_at())
                .info(req.getInfo())
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        return toResponse(experienceRepo.save(exp));
    }

    @Override
    public ExperienceCandidateResponse updateExperience(Long id, FormExperienceCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ExperienceCandidate exp = experienceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (exp.getCandidate() == null || !exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own experience");
        }

        exp.setPosition(req.getPosition());
        exp.setCompany(req.getCompany());
        exp.setStarted_at(req.getStarted_at());
        exp.setEnd_at(req.getEnd_at());
        exp.setInfo(req.getInfo());
        exp.setUpdated_at(new Date());

        return toResponse(experienceRepo.save(exp));
    }

    @Override
    public void deleteExperience(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();

        ExperienceCandidate exp = experienceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Experience not found"));

        if (exp.getCandidate() == null || !exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own experience");
        }

        experienceRepo.delete(exp);
    }

    private ExperienceCandidateResponse toResponse(ExperienceCandidate exp) {
        return ExperienceCandidateResponse.builder()
                .id(exp.getId())
                .position(exp.getPosition())
                .company(exp.getCompany())
                .started_at(exp.getStarted_at())
                .end_at(exp.getEnd_at())
                .info(exp.getInfo())
                .created_at(exp.getCreated_at())
                .updated_at(exp.getUpdated_at())
                .build();
    }
}
