package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.model.SkillsCandidate;
import com.ra.base_spring_boot.repository.ISkillsCandidateRepository;
import com.ra.base_spring_boot.services.ISkillsCandidateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service 
public class SkillsCandidateServiceImpl implements ISkillsCandidateService {

    private final ISkillsCandidateRepository skillsCandidateRepository;

    
    @Autowired
    public SkillsCandidateServiceImpl(ISkillsCandidateRepository skillsCandidateRepository) {
        this.skillsCandidateRepository = skillsCandidateRepository;
    }

    @Override
    public List<SkillsCandidate> findAll() {
        return skillsCandidateRepository.findAll();
    }

    @Override
    public Optional<SkillsCandidate> findById(Long id) {
        return skillsCandidateRepository.findById(id);
    }

    @Override
    public SkillsCandidate save(SkillsCandidate skillsCandidate) {
        
        return skillsCandidateRepository.save(skillsCandidate);
    }

    @Override
    public void deleteById(Long id) {
        skillsCandidateRepository.deleteById(id);
    }

    @Override
    public List<SkillsCandidate> findAllByCandidateId(Long candidateId) {
        return skillsCandidateRepository.findAllByCandidate_Id(candidateId);
    }
}