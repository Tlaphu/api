package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.repository.LevelJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelJobService {
    private final LevelJobRepository repo;

    public LevelJobService(LevelJobRepository repo) {
        this.repo = repo;
    }

    public List<LevelJob> getAll() { return repo.findAll(); }
    public LevelJob getById(String id) { return repo.findById(id).orElse(null); }
    public LevelJob save(LevelJob lj) { return repo.save(lj); }
    public void delete(String id) { repo.deleteById(id); }
}