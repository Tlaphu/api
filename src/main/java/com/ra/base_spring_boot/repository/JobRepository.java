package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 
import java.util.List;
import java.util.Date;

public interface JobRepository extends JpaRepository<Job, Long> {
    
    List<Job> findTop10ByOrderBySalaryDesc();

    @Query("SELECT j FROM Job j WHERE j.expire_at >= :currentDate OR j.expire_at IS NULL")
    List<Job> findAllActiveJobs(@Param("currentDate") Date currentDate); 
    
  
    @Query("SELECT j FROM Job j WHERE j.expire_at < :expireAt AND j.status <> :status")
    List<Job> findJobsToExpire(@Param("expireAt") Date expireAt, @Param("status") String status); 
    List<Job> findByCompanyId(Long companyId);
}