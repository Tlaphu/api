package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Job;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findTop10ByOrderBySalaryDesc();
}
