package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormCertificateCandidate;
import com.ra.base_spring_boot.dto.req.FromExperienceCandidate;
import com.ra.base_spring_boot.dto.resp.CertificateCandidateResponse;

import java.util.List;
public interface ICertificateCandidateService {
    List<CertificateCandidateResponse> getCertificate();
    CertificateCandidateResponse createCertificate(FormCertificateCandidate req);

    CertificateCandidateResponse updateCertificate(String id, FormCertificateCandidate req);
    void deleteCertificate(String id);
}
