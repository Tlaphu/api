package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.EducationCandidate;
import com.ra.base_spring_boot.repository.IEducationCandidateRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.IEducationCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationCandidateServiceImpl implements IEducationCandidateService {

    private final IEducationCandidateRepository educationRepo;
    private final JwtProvider jwtProvider;

    @Override
    public List<EducationCandidateResponse> getAllByCandidate(Long ignoredCandidateId) {
        Candidate current = jwtProvider.getCurrentCandidate();
        return educationRepo.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EducationCandidateResponse createByCandidate(String ignoredCandidateId, FormEducationCandidate request) {
        Candidate current = jwtProvider.getCurrentCandidate();

        EducationCandidate edu = EducationCandidate.builder()
                .candidate(current)
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
    public EducationCandidateResponse updateByCandidate(Long id, String ignoredCandidateId, FormEducationCandidate request) {
        Candidate current = jwtProvider.getCurrentCandidate();

        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!edu.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own education record");
        }

        edu.setName_education(request.getName_education());
        edu.setMajor(request.getMajor());
        edu.setGPA(request.getGPA());
        edu.setStarted_at(request.getStartedAt());
        edu.setEnd_at(request.getEndAt());
        edu.setInfo(request.getInfo());
        edu.setUpdated_at(new Date());

        return toResponse(educationRepo.save(edu));
    }

    @Override
    public void deleteByCandidate(Long id, String ignoredCandidateId) {
        Candidate current = jwtProvider.getCurrentCandidate();

        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));

        if (!edu.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own education record");
        }

        educationRepo.delete(edu);
    }

    private EducationCandidateResponse toResponse(EducationCandidate edu) {
        return EducationCandidateResponse.builder()
                .id(edu.getId())
                .nameEducation(edu.getName_education())
                .major(edu.getMajor())
                .startedAt(edu.getStarted_at())
                .endAt(edu.getEnd_at())
                .info(edu.getInfo())
                .createdAt(edu.getCreated_at())
                .updatedAt(edu.getUpdated_at())
                .build();
    }
}
