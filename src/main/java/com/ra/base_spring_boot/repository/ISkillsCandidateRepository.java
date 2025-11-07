package com.ra.base_spring_boot.repository;
import com.ra.base_spring_boot.model.SkillsCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ISkillsCandidateRepository extends JpaRepository<SkillsCandidate, Long> {
    List<SkillsCandidate> findAllByCandidate_Id(Long candidateId);
    void deleteByCandidateCVId(Long cvId);
}
