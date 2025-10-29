package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CandidateCV;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ICandidateCVRepository extends JpaRepository<CandidateCV, Long> {
    
    
    List<CandidateCV> findByCandidate_Id(Long candidateId);
    
    
    Optional<CandidateCV> findByIdAndCandidate_Id(Long id, Long candidateId);
}
