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
    public LevelJobRelation getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody LevelJobRelation relation) {

        return ResponseEntity.ok(service.save(relation));
    }

    @PutMapping("/{id}")
    public LevelJobRelation update(@PathVariable Long id, @RequestBody LevelJobRelation relation) { // <--- Đã sửa String -> Long
        relation.setId(id);
        return service.save(relation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        LevelJobRelation existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404)
                    .body("No relation exists with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted LevelJobRelation with id: " + id);
    }
}
