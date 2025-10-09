package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.services.LevelJobService;

import org.springframework.http.ResponseEntity;
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
    public Object create(@RequestBody LevelJob lj) {
    LevelJob existed = service.getById(lj.getId());
    if (existed != null) {
        return ResponseEntity
            .status(409)
            .body("LevelJob has existed with id: " + lj.getId());
    }
    return service.save(lj);
    }
    @PutMapping("/{id}")
    public LevelJob update(@PathVariable String id, @RequestBody LevelJob lj) {
        lj.setId(id);
        return service.save(lj);
    }

   @DeleteMapping("/{id}")
public Object delete(@PathVariable String id) {
    LevelJob existed = service.getById(id);
    if (existed == null) {
        return ResponseEntity
            .status(404)
            .body("No LevelJob exists to delete with id: " + id);
    }
    service.delete(id);
    return ResponseEntity.ok("Successfully deleted LevelJob with id: " + id);
}
}