package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormCertificateCandidate;
import com.ra.base_spring_boot.dto.resp.CertificateCandidateResponse;
import com.ra.base_spring_boot.exception.HttpAccessDenied;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.CertificateCandidate;
import com.ra.base_spring_boot.repository.ICertificateCandidateRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ICertificateCandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CertificateCandidateServiceImpl implements ICertificateCandidateService {
    private final ICertificateCandidateRepository iCertificateCandidateRepository;
    private final JwtProvider jwtProvider;

    @Override
    public List<CertificateCandidateResponse> getCertificate(){
        Candidate current = jwtProvider.getCurrentCandidate();
        return iCertificateCandidateRepository.findAllByCandidate_Id(current.getId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CertificateCandidateResponse createCertificate(FormCertificateCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();

        CertificateCandidate exp = CertificateCandidate.builder()
                .candidate(current)
                .name(req.getName())
                .organization(req.getOrganization())
                .started_at(req.getStarted_at())
                .end_at(req.getEnd_at())
                .info(req.getInfo())
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        return toResponse(iCertificateCandidateRepository.save(exp));
    }

    @Override
    public CertificateCandidateResponse updateCertificate(Long id, FormCertificateCandidate req) {
        Candidate current = jwtProvider.getCurrentCandidate();
        CertificateCandidate exp = iCertificateCandidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
                
        if (!exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only update your own certificate");
        }

        exp.setName(req.getName());
        exp.setOrganization(req.getOrganization());
        exp.setStarted_at(req.getStarted_at());
        exp.setEnd_at(req.getEnd_at());
        exp.setInfo(req.getInfo());
        exp.setUpdated_at(new Date());

        return toResponse(iCertificateCandidateRepository.save(exp));
    }

    @Override
    public void deleteCertificate(Long id) {
        Candidate current = jwtProvider.getCurrentCandidate();
        CertificateCandidate exp = iCertificateCandidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (!exp.getCandidate().getId().equals(current.getId())) {
            throw new HttpAccessDenied("Access denied: You can only delete your own certificate");
        }

        iCertificateCandidateRepository.delete(exp);
    }

    private CertificateCandidateResponse toResponse (CertificateCandidate cer){
        return CertificateCandidateResponse.builder()
                .id(cer.getId())
                .name(cer.getName())
                .organization(cer.getOrganization())
                .started_at(cer.getStarted_at())
                .end_at(cer.getEnd_at())
                // NOTE: Removed duplicate '.organization(cer.getOrganization())' line here.
                .info(cer.getInfo())
                .created_at(cer.getCreated_at())
                .updated_at(cer.getUpdated_at())
                .build();
    }
}