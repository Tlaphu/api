package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.TypeJob;
import com.ra.base_spring_boot.repository.TypeJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeJobService {

    private final TypeJobRepository repo;

    public TypeJobService(TypeJobRepository repo) {
        this.repo = repo;
    }

    public List<TypeJob> getAll() {
        return repo.findAll();
    }


    public TypeJob getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public TypeJob save(TypeJob tj) {
        return repo.save(tj);
    }

   
    public void delete(Long id) {
        repo.deleteById(id);
    }
}