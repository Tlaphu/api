package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.SkillsCandidate;
import java.util.List;
import java.util.Optional;

public interface ISkillsCandidateService {

  
    List<SkillsCandidate> findAll();

    
    Optional<SkillsCandidate> findById(Long id);

 
    SkillsCandidate save(SkillsCandidate skillsCandidate);

    
    void deleteById(Long id);


    List<SkillsCandidate> findAllByCandidateId(Long candidateId);
}