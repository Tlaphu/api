package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.EducationCandidate;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.IEducationCandidateRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider; // Cần import JwtProvider
import com.ra.base_spring_boot.services.IEducationCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationCandidateServiceImpl implements IEducationCandidateService {

    private final IEducationCandidateRepository educationRepo;
    private final ICandidateRepository candidateRepo;
    private final JwtProvider jwtProvider;

    private EducationCandidateResponse toResponse(EducationCandidate edu) {
        return EducationCandidateResponse.builder()
                .id(edu.getId())
                .nameEducation(edu.getName_education())
                .major(edu.getMajor())
                .startedAt(edu.getStarted_at())
                .endAt(edu.getEnd_at())
                .GPA(edu.getGPA())
                .info(edu.getInfo())
                .createdAt(edu.getCreated_at())
                .updatedAt(edu.getUpdated_at())
                .build();
    }

    @Override
    public List<EducationCandidateResponse> getAllByCandidate(Long candidateId) {

        List<EducationCandidate> list = educationRepo.findAllByCandidate_Id(candidateId);

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EducationCandidateResponse createByCandidate(FormEducationCandidate request) {

        Candidate current = jwtProvider.getCurrentCandidate();

        EducationCandidate edu = EducationCandidate.builder()
                .candidate(current)
                .candidateCV(null)
                .name_education(request.getName_education())
                .major(request.getMajor())
                .GPA(request.getGPA())
                .started_at(request.getStartedAt())
                .end_at(request.getEndAt())
                .info(request.getInfo())
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        return toResponse(educationRepo.save(edu));
    }

    @Override
    public EducationCandidateResponse updateByCandidate(Long id, FormEducationCandidate request) {

        Candidate current = jwtProvider.getCurrentCandidate();
        Long currentCandidateId = current.getId();

        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (edu.getCandidate() == null || !edu.getCandidate().getId().equals(currentCandidateId)) {
            throw new HttpAccessDenied("Unauthorized: cannot edit other candidate’s education");
        }

        edu.setName_education(request.getName_education());
        edu.setMajor(request.getMajor());
        edu.setStarted_at(request.getStartedAt());
        edu.setEnd_at(request.getEndAt());
        edu.setGPA(request.getGPA());
        edu.setInfo(request.getInfo());
        edu.setUpdated_at(new Date());

        return toResponse(educationRepo.save(edu));
    }

    @Override
    public void deleteByCandidate(Long id) {

        Candidate current = jwtProvider.getCurrentCandidate();
        Long currentCandidateId = current.getId();

        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (edu.getCandidate() == null || !edu.getCandidate().getId().equals(currentCandidateId)) {

            throw new HttpAccessDenied("Unauthorized: cannot delete other candidate’s education");
        }

        educationRepo.delete(edu);
    }
}
