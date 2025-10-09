package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.LevelJobRelation;
import com.ra.base_spring_boot.services.LevelJobRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/level-job-relation")
public class LevelJobRelationController {

    private final LevelJobRelationService service;

    public LevelJobRelationController(LevelJobRelationService service) {
        this.service = service;
    }

    @GetMapping
    public List<LevelJobRelation> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public LevelJobRelation getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LevelJobRelation relation) {
        LevelJobRelation existed = service.getById(relation.getId());
        if (existed != null) {
            return ResponseEntity.status(409)
                    .body("Relation already exists with id: " + relation.getId());
        }
        return ResponseEntity.ok(service.save(relation));
    }

    @PutMapping("/{id}")
    public LevelJobRelation update(@PathVariable String id, @RequestBody LevelJobRelation relation) {
        relation.setId(id);
        return service.save(relation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        LevelJobRelation existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404)
                    .body("No relation exists with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted LevelJobRelation with id: " + id);
    }
}
