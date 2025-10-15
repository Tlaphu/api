package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CertificateCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICertificateCandidateRepository extends JpaRepository<CertificateCandidate, String > {
    List<CertificateCandidate> findAllByCandidate_Id(Long candidateId);
}
