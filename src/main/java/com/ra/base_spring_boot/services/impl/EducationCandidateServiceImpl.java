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
<<<<<<< HEAD
    public List<EducationCandidateResponse> getAllByCandidate(Long candidateId) {
=======
    public List<EducationCandidateResponse> getAllByCandidate() {
        Candidate current = jwtProvider.getCurrentCandidate();
>>>>>>> 1f30a81d790ecab57c3ee282eea67f509f150ff1

        List<EducationCandidate> list = educationRepo.findAllByCandidate_Id(candidateId);

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EducationCandidateResponse createByCandidate(FormEducationCandidate request) {

        Candidate current = jwtProvider.getCurrentCandidate();

        EducationCandidate edu = EducationCandidate.builder()
                .candidate(current)
                .candidateCV(null)
                .nameEducation(request.getNameEducation())
                .major(request.getMajor())
                .gpa(request.getGpa())
                .startedAt(request.getStartedAt())
                .endAt(request.getEndAt())
                .info(request.getInfo())
                .createdAt(new Date())
                .updatedAt(new Date())
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

        edu.setNameEducation(request.getNameEducation());
        edu.setMajor(request.getMajor());
        edu.setStartedAt(request.getStartedAt());
        edu.setEndAt(request.getEndAt());
        edu.setGpa(request.getGpa());
        edu.setInfo(request.getInfo());
        edu.setUpdatedAt(new Date());

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
<<<<<<< HEAD
}
=======

    private EducationCandidateResponse toResponse(EducationCandidate edu) {
        return EducationCandidateResponse.builder()
                .id(edu.getId())
                .nameEducation(edu.getNameEducation())
                .major(edu.getMajor())
                .startedAt(edu.getStartedAt())
                .endAt(edu.getEndAt())
                .gpa(edu.getGpa())
                .info(edu.getInfo())
                .createdAt(edu.getCreatedAt())
                .updatedAt(edu.getUpdatedAt())
                .build();
    }
}
>>>>>>> 1f30a81d790ecab57c3ee282eea67f509f150ff1
