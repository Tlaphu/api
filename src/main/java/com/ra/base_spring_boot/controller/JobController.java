package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Job;
import com.ra.base_spring_boot.services.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/job")
public class JobController {

    private final JobService service;

    public JobController(JobService service) {
        this.service = service;
    }


    @GetMapping
    public List<Job> getAll() {
        return service.getAll();
    }


    @GetMapping("/{id}")
    public Object getById(@PathVariable String id) {
        Job job = service.getById(id);
        if (job == null) {
            return ResponseEntity
                    .status(404)
                    .body("Job not found with id: " + id);
        }
        return ResponseEntity.ok(job);
    }

    @PostMapping
    public Object create(@RequestBody Job job) {
        Job existed = service.getById(job.getId());
        if (existed != null) {
            return ResponseEntity
                    .status(409)
                    .body("Job already exists with id: " + job.getId());
        }
        return ResponseEntity.ok(service.save(job));
    }


    @PutMapping("/{id}")
    public Object update(@PathVariable String id, @RequestBody Job job) {
        Job existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity
                    .status(404)
                    .body("Job not found with id: " + id);
        }
        job.setId(id);
        return ResponseEntity.ok(service.save(job));
    }


    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id) {
        Job existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity
                    .status(404)
                    .body("No Job to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted Job with id: " + id);
    }


    @GetMapping("/featured")
    public List<Job> getFeaturedJobs() {
        return service.getFeaturedJobs();
    }
}
