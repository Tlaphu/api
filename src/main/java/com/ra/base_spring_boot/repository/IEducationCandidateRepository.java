package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.EducationCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IEducationCandidateRepository extends JpaRepository<EducationCandidate, Long> {
    List<EducationCandidate> findAllByCandidate_Id(Long candidateId);
}
