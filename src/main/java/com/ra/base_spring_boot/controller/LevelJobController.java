package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.services.LevelJobService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/level-job")
public class LevelJobController {
    private final LevelJobService service;

    public LevelJobController(LevelJobService service) {
        this.service = service;
    }

    @GetMapping
    public List<LevelJob> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public LevelJob getById(@PathVariable String id) { return service.getById(id); }

    @PostMapping
    public LevelJob create(@RequestBody LevelJob lj) { return service.save(lj); }

    @PutMapping("/{id}")
    public LevelJob update(@PathVariable String id, @RequestBody LevelJob lj) {
        lj.setId(id);
        return service.save(lj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { service.delete(id); }
}