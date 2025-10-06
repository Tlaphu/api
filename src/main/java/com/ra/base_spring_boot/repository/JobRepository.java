package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {}
