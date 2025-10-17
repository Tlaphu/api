package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FromEducationCandidate;
import com.ra.base_spring_boot.dto.resp.EducationCandidateResponse;
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
    private final ICandidateRepository candidateRepo;

    @Override
    public List<EducationCandidateResponse> getAllByCandidate(Long candidateId) {
        List<EducationCandidate> list = educationRepo.findAllByCandidate_Id(candidateId);
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    // Đã hoàn nguyên tham số về String để khớp với IEducationCandidateService
    public EducationCandidateResponse createByCandidate(String candidateId, FromEducationCandidate request) {
        // Chuyển đổi String ID sang Long để tìm kiếm trong Repository
        Long idAsLong = Long.parseLong(candidateId);
        
        Candidate candidate = candidateRepo.findById(idAsLong)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        EducationCandidate edu = EducationCandidate.builder()
                .candidate(candidate)
                .name_education(request.getName_education())
                .major(request.getMajor())
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
    // Đã hoàn nguyên tham số về String để khớp với IEducationCandidateService
    public EducationCandidateResponse updateByCandidate(Long id, String candidateId, FromEducationCandidate request) {
        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        
        // Chuyển đổi String ID sang Long để so sánh
        Long candidateIdAsLong = Long.parseLong(candidateId);

        // So sánh hai Long ID
        if (!edu.getCandidate().getId().equals(candidateIdAsLong)) {
            throw new RuntimeException("Unauthorized: cannot edit other candidate’s education");
        }

        // ... Logic cập nhật ...
        edu.setName_education(request.getName_education());
        edu.setMajor(request.getMajor());
        edu.setStarted_at(request.getStartedAt());
        edu.setEnd_at(request.getEndAt());
        edu.setInfo(request.getInfo());
        edu.setUpdated_at(new Date());

        educationRepo.save(edu);
        return toResponse(edu);
    }

    @Override
    // Đã hoàn nguyên tham số về String để khớp với IEducationCandidateService
    public void deleteByCandidate(Long id, String candidateId) {
        EducationCandidate edu = educationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        
        // Chuyển đổi String ID sang Long để so sánh
        Long candidateIdAsLong = Long.parseLong(candidateId);

     
        if (!edu.getCandidate().getId().equals(candidateIdAsLong)) {
            throw new RuntimeException("Unauthorized: cannot delete other candidate’s education");
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
