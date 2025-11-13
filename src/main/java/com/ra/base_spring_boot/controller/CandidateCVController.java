package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormCandidateCV;
import com.ra.base_spring_boot.dto.req.FormCandidateCVArchive;
import com.ra.base_spring_boot.dto.resp.CandidateCVResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.CandidateCV;
import com.ra.base_spring_boot.model.CandidateCVArchive;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ICandidateCVService;
import com.ra.base_spring_boot.services.impl.CandidateCVServiceImpl;
import lombok.RequiredArgsConstructor;
// ⭐ Import cần thiết cho PDF
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/candidate/cv")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_CANDIDATE')")
public class CandidateCVController {

    private final ICandidateCVService candidateCVService;
    private final CandidateCVServiceImpl candidateCVServiceImpl;

    private final JwtProvider jwtProvider;

    private Long getCurrentCandidateId() {
        Candidate candidate = jwtProvider.getCurrentCandidate();
        if (candidate == null) {
            throw new RuntimeException("Candidate not authenticated.");
        }
        return candidate.getId();
    }


    @PostMapping
    public ResponseEntity<ResponseWrapper<CandidateCVResponse>> createNewCV(@RequestBody FormCandidateCV cvForm) {
        Long candidateId = getCurrentCandidateId();
        CandidateCV newCV = candidateCVService.createNewCV(cvForm, candidateId);

        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(newCV);


        ResponseWrapper<CandidateCVResponse> response = ResponseWrapper.<CandidateCVResponse>builder()
                .status(HttpStatus.CREATED)
                .code(HttpStatus.CREATED.value())
                .data(cvResponse)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<CandidateCVResponse>> getAllCandidateCVs() {
        Long candidateId = getCurrentCandidateId();
        List<CandidateCV> cvList = candidateCVService.getAllCVsByCandidate(candidateId);

        List<CandidateCVResponse> responseList = cvList.stream()
                .map(candidateCVServiceImpl::mapToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{cvId}")
    public ResponseEntity<CandidateCVResponse> getCVById(@PathVariable Long cvId) {
        Long candidateId = getCurrentCandidateId();
        CandidateCV cvEntity = candidateCVService.getCVById(cvId, candidateId);

        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(cvEntity);

        return ResponseEntity.ok(cvResponse);
    }

    @PutMapping("/{cvId}")
    public ResponseEntity<CandidateCVResponse> updateCV(@PathVariable Long cvId, @RequestBody FormCandidateCV cvForm) {
        Long candidateId = getCurrentCandidateId();
        CandidateCV updatedCandidate = candidateCVService.updateCV(cvId, cvForm, candidateId);

        CandidateCVResponse cvResponse = candidateCVServiceImpl.mapToResponse(updatedCandidate);

        return ResponseEntity.ok(cvResponse);
    }

    @DeleteMapping("/{cvId}")
    public ResponseEntity<?> deleteCV(@PathVariable Long cvId) {
        Long candidateId = getCurrentCandidateId();
        candidateCVService.deleteCV(cvId, candidateId);

        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{cvId}/export/pdf")
    public ResponseEntity<byte[]> exportCVToPdf(@PathVariable Long cvId) {
        Long candidateId = getCurrentCandidateId();


        byte[] pdfBytes = candidateCVService.generatePdfFromCV(cvId, candidateId);


        if (pdfBytes == null || pdfBytes.length == 0) {

            throw new HttpBadRequest("Failed to generate PDF content.");
        }

        CandidateCV cvEntity = candidateCVService.getCVById(cvId, candidateId);
        String filename = cvEntity.getTitle().replaceAll("[^a-zA-Z0-9_-]", "") + "_CV.pdf";

        HttpHeaders headers = new HttpHeaders();


        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", filename);
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(
                pdfBytes,
                headers,
                HttpStatus.OK
        );
    }

    @DeleteMapping("/archive/{archiveId}")
    public ResponseEntity<?> deleteCVArchive(@PathVariable Long archiveId) {

        candidateCVService.deleteCVArchive(archiveId);

        return ResponseEntity.noContent().build();
    }


}