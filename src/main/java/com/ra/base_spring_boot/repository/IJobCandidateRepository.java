package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.JobCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface IJobCandidateRepository extends JpaRepository<JobCandidate, Long> {
    
     List<JobCandidate> findByJobId(Long jobId);
     List<JobCandidate> findByCandidateId(Long candidateId);
}