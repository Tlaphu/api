package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.TypeJobRelation;
import com.ra.base_spring_boot.services.TypeJobRelationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/type-job-relation")
public class TypeJobRelationController {

    private final TypeJobRelationService service;

    public TypeJobRelationController(TypeJobRelationService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeJobRelation> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Object getById(@PathVariable Long id) {
        TypeJobRelation relation = service.getById(id);
        if (relation == null) {
            return ResponseEntity.status(404)
                    .body("No TypeJobRelation found with id: " + id);
        }
        return relation;
    }

    @PostMapping
    public Object create(@RequestBody TypeJobRelation relation) {

        return service.save(relation);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody TypeJobRelation relation) {
        relation.setId(id);
        return service.save(relation);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        TypeJobRelation existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity.status(404)
                    .body("No TypeJobRelation exists to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted TypeJobRelation with id: " + id);
    }
}
