package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.EducationCandidate;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.IEducationCandidateRepository;
import com.ra.base_spring_boot.services.IEducationCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EducationCandidateServiceImpl implements IEducationCandidateService {

    private final IEducationCandidateRepository educationRepo;
    private final ICandidateRepository candidateRepo; // Giữ lại để tìm Candidate nếu cần (thường không cần trong Service này)

    @Override
    public List<EducationCandidateResponse> getAllByCandidate(Long candidateId) {
        // Giả định Repository có phương thức findAllByCandidate_Id
        List<EducationCandidate> list = educationRepo.findAllByCandidate_Id(candidateId);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public EducationCandidateResponse createByCandidate(String candidateId, FormEducationCandidate request) {
        
        Long idAsLong = Long.parseLong(candidateId);
        
        
        Candidate candidate = candidateRepo.findById(idAsLong)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        EducationCandidate edu = EducationCandidate.builder()
                .candidate(candidate)       
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

        educationRepo.save(edu);
        return toResponse(edu);
    }

    @Override
    public EducationCandidateResponse updateByCandidate(Long id, String candidateId, FormEducationCandidate request) {
        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        
        Long candidateIdAsLong = Long.parseLong(candidateId);

        // Kiểm tra quyền sở hữu
        if (edu.getCandidate() == null || !edu.getCandidate().getId().equals(candidateIdAsLong)) {
            throw new HttpAccessDenied("Unauthorized: cannot edit other candidate’s education");
        }

        edu.setName_education(request.getName_education());
        edu.setMajor(request.getMajor());
        edu.setStarted_at(request.getStartedAt());
        edu.setEnd_at(request.getEndAt());
        edu.setGPA(request.getGPA());
        edu.setInfo(request.getInfo());
        edu.setUpdated_at(new Date());

        educationRepo.save(edu);
        return toResponse(edu);
    }

    @Override
    public void deleteByCandidate(Long id, String candidateId) {
        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        
        Long candidateIdAsLong = Long.parseLong(candidateId);

        // Kiểm tra quyền sở hữu
        if (edu.getCandidate() == null || !edu.getCandidate().getId().equals(candidateIdAsLong)) {
            throw new HttpAccessDenied("Unauthorized: cannot delete other candidate’s education");
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
                .GPA(edu.getGPA())
                .info(edu.getInfo())
                .createdAt(edu.getCreated_at())
                .updatedAt(edu.getUpdated_at())
                .build();
    }
}