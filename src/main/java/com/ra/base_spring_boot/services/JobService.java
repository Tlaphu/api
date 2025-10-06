package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    private final JobRepository repo;

    public JobService(JobRepository repo) {
        this.repo = repo;
    }

    public List<Job> getAll() { return repo.findAll(); }
    public Job getById(String id) { return repo.findById(id).orElse(null); }
    public Job save(Job job) { return repo.save(job); }
    public void delete(String id) { repo.deleteById(id); }
}