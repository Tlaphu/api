package com.ra.base_spring_boot.controller;


import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormCertificateCandidate;
import com.ra.base_spring_boot.dto.resp.CertificateCandidateResponse;
import com.ra.base_spring_boot.services.ICertificateCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/candidate/certificate")
@RequiredArgsConstructor
public class CertificateCandidateController {

    private final ICertificateCandidateService certificateCandidateService;

    /**
     * @apiNote
     */
    @GetMapping
    public ResponseEntity<?> getCertificate() {
        List<CertificateCandidateResponse> certificate = certificateCandidateService.getCertificate();
        return ResponseEntity.ok(
                ResponseWrapper.<List<CertificateCandidateResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(certificate)
                        .build()
        );
    }

    /**
     * @apiNote
     */
    @PostMapping
    public ResponseEntity<?> createExperience(@RequestBody FormCertificateCandidate request) {
        CertificateCandidateResponse newExp = certificateCandidateService.createCertificate(request);
        return ResponseEntity.created(URI.create("/api/v1/candidate/experiences"))
                .body(ResponseWrapper.<CertificateCandidateResponse>builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data(newExp)
                        .build()
                );
    }

    /**
     * @apiNote
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExperience(
            @PathVariable Long id,
            @RequestBody FormCertificateCandidate request) {

        CertificateCandidateResponse updated = certificateCandidateService.updateCertificate(id, request);
        return ResponseEntity.ok(
                ResponseWrapper.<CertificateCandidateResponse>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(updated)
                        .build()
        );
    }

    /**
     * @apiNote
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperience(@PathVariable Long id) {
        certificateCandidateService.deleteCertificate(id);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Certificate deleted successfully")
                        .build()
        );
    }
}

