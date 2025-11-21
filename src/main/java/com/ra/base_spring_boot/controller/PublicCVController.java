package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.CandidateCVResponse;
import com.ra.base_spring_boot.model.CandidateCV;
import com.ra.base_spring_boot.services.ICandidateCVService;
import com.ra.base_spring_boot.services.impl.CandidateCVServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/public/cv")
@RequiredArgsConstructor
public class PublicCVController {

    private final ICandidateCVService candidateCVService;
    private final CandidateCVServiceImpl candidateCVServiceImpl; // Dùng để map DTO


    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CandidateCVResponse>>> getAllPublicCVs() {
        // 1. Lấy danh sách CandidateCV từ Service bằng cách truyền tham số true
        List<CandidateCV> publicCVList = candidateCVService.getCVsByPublicStatus(true);

        // 2. Map sang Response DTO (CandidateCVResponse)
        List<CandidateCVResponse> responseList = publicCVList.stream()
                .map(candidateCVServiceImpl::mapToResponse)
                .collect(Collectors.toList());

        // 3. Đóng gói Response
        ResponseWrapper<List<CandidateCVResponse>> response = ResponseWrapper.<List<CandidateCVResponse>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(responseList)
                .build();

        return ResponseEntity.ok(response);
    }


    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ResponseWrapper<List<CandidateCVResponse>>> getAllPublicCVsByCandidate(
            @PathVariable Long candidateId) {

        // 1. Lấy danh sách CandidateCV từ Service bằng Candidate ID
        List<CandidateCV> publicCVList = candidateCVService.getAllPublicCVsByCandidateId(candidateId);

        // 2. Map sang Response DTO
        List<CandidateCVResponse> responseList = publicCVList.stream()
                .map(candidateCVServiceImpl::mapToResponse)
                .collect(Collectors.toList());

        // 3. Đóng gói Response
        ResponseWrapper<List<CandidateCVResponse>> response = ResponseWrapper.<List<CandidateCVResponse>>builder()
                .status(HttpStatus.OK)
                .code(HttpStatus.OK.value())
                .data(responseList)
                .build();

        return ResponseEntity.ok(response);
    }
}