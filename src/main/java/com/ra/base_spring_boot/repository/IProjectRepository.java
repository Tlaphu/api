package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.ProjectCandidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProjectRepository extends JpaRepository<ProjectCandidate , Long>{
    List<ProjectCandidate> findAllByCandidate_Id(Long candidateId);
}
