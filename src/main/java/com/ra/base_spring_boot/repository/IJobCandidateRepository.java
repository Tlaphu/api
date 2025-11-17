package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.JobCandidate;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Date;
import java.util.List;

@Repository
public interface IJobCandidateRepository extends JpaRepository<JobCandidate, Long> {

    // Tìm kiếm các đơn ứng tuyển theo ID công việc
    List<JobCandidate> findByJobId(Long jobId);


    List<JobCandidate> findByCandidateId(Long candidateId);


    boolean existsByJob_IdAndCandidateCV_Id(Long jobId, Long cvId);


    void deleteByCandidateCVId(Long cvId);


    @Query("SELECT COUNT(jc) FROM JobCandidate jc " +
            "WHERE jc.candidate.id = :candidateId AND jc.created_at >= :startDate AND jc.created_at < :endDate")
    Integer countApplicationsInMonth(@Param("candidateId") Long candidateId,
                                     @Param("startDate") Date startDate,
                                     @Param("endDate") Date endDate);
    @Modifying
    @Transactional
    @Query("DELETE FROM JobCandidate jc WHERE jc.job.id = :jobId")
    void deleteByJobId(@Param("jobId") Long jobId);
    boolean existsByJob_IdAndCandidate_Id(Long jobId, Long candidateId);
}