package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.model.TypeJob;
import com.ra.base_spring_boot.model.TypeJobRelation;
import com.ra.base_spring_boot.repository.JobRepository;
import com.ra.base_spring_boot.repository.TypeJobRelationRepository;
import com.ra.base_spring_boot.repository.TypeJobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeJobRelationService {

    private final TypeJobRelationRepository repository;
    private final JobRepository jobRepository;
    private final TypeJobRepository typeJobRepository;

    public TypeJobRelationService(TypeJobRelationRepository repository,
                                  JobRepository jobRepository,
                                  TypeJobRepository typeJobRepository) {
        this.repository = repository;
        this.jobRepository = jobRepository;
        this.typeJobRepository = typeJobRepository;
    }

    public List<TypeJobRelation> getAll() {
        return repository.findAll();
    }

    public TypeJobRelation getById(String id) {
        return repository.findById(id).orElse(null);
    }

    public TypeJobRelation save(TypeJobRelation relation) {
    
        if (relation.getJob() != null && relation.getJob().getId() != null) {
            Job job = jobRepository.findById(relation.getJob().getId()).orElse(null);
            relation.setJob(job);
        }

        if (relation.getTypeJob() != null && relation.getTypeJob().getId() != null) {
            TypeJob typeJob = typeJobRepository.findById(relation.getTypeJob().getId()).orElse(null);
            relation.setTypeJob(typeJob);
        }

        return repository.save(relation);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}
