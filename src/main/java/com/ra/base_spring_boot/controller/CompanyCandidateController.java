package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.resp.JobCandidateResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ICandidateCVService;
import com.ra.base_spring_boot.model.AccountCompany;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/company/cv")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
public class CompanyCandidateController {

    private final ICandidateCVService candidateCVService;
    private final JwtProvider jwtProvider;

    private Long getCurrentCompanyId() {
        AccountCompany company = jwtProvider.getCurrentAccountCompany();
        if (company == null) {
            throw new NoSuchElementException("Company not authenticated.");
        }
        return company.getId();
    }


    @GetMapping("/{cvId}/download")
    public ResponseEntity<byte[]> downloadCandidateCv(@PathVariable Long cvId) {
        Long companyId = getCurrentCompanyId();


        byte[] pdfBytes = candidateCVService.downloadCvForCompany(cvId, companyId);

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new HttpBadRequest("Failed to retrieve CV content.");
        }

        HttpHeaders headers = new HttpHeaders();

        String filename = "CV_" + cvId + ".pdf";

        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", filename);
        headers.setContentLength(pdfBytes.length);

        return new ResponseEntity<>(
                pdfBytes,
                headers,
                HttpStatus.OK
        );
    }
}