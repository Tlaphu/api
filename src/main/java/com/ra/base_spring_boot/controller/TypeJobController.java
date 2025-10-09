package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.TypeJob;
import com.ra.base_spring_boot.services.TypeJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/type-job")
public class TypeJobController {
    private final TypeJobService service;

    public TypeJobController(TypeJobService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeJob> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public TypeJob getById(@PathVariable String id) { return service.getById(id); }

    @PostMapping
    public Object create(@RequestBody TypeJob tj) {
        TypeJob existed = service.getById(tj.getId());
        if (existed != null) {
            return ResponseEntity
                .status(409)
                .body("TypeJob already exists with id: " + tj.getId());
        }
        return service.save(tj);
    }

    @PutMapping("/{id}")
    public TypeJob update(@PathVariable String id, @RequestBody TypeJob tj) {
        tj.setId(id);
        return service.save(tj);
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable String id) {
        TypeJob existed = service.getById(id);
        if (existed == null) {
            return ResponseEntity
                .status(404)
                .body("No TypeJob exists to delete with id: " + id);
        }
        service.delete(id);
        return ResponseEntity.ok("Successfully deleted TypeJob with id: " + id);
    }
}