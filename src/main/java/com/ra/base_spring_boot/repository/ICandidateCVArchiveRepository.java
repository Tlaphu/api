package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.CandidateCVArchive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICandidateCVArchiveRepository extends JpaRepository<CandidateCVArchive, Long> {


    Optional<CandidateCVArchive> findByCandidateCVIdAndCandidateId(Long candidateCVId, Long candidateId);


    void deleteByCandidateCVId(Long candidateCVId);


    void deleteByCandidateId(Long candidateId);
}