package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.LevelJobRelation;
import com.ra.base_spring_boot.repository.LevelJobRelationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelJobRelationService {

    private final LevelJobRelationRepository repository;

    public LevelJobRelationService(LevelJobRelationRepository repository) {
        this.repository = repository;
    }

    public List<LevelJobRelation> getAll() {
        return repository.findAll();
    }

 
    public LevelJobRelation getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public LevelJobRelation save(LevelJobRelation relation) {
        return repository.save(relation);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}