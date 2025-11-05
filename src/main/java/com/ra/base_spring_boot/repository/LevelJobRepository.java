package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.LevelJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelJobRepository extends JpaRepository<LevelJob, Long> {
    Optional<LevelJob> findByName(String name);
    boolean existsByNameIgnoreCase(String name);
    Optional<LevelJob> findByNameIgnoreCase(String name);

}
