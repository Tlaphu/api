package com.ra.base_spring_boot.repository;


import com.ra.base_spring_boot.model.ExperienceCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IExperienceCandidateRepository extends JpaRepository<ExperienceCandidate, Long> {
    List<ExperienceCandidate> findAllByCandidate_Id(Long candidateId);
}
