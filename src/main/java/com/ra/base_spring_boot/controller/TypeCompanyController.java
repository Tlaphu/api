package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.LevelJob;
import com.ra.base_spring_boot.model.TypeCompany;
import com.ra.base_spring_boot.services.TypeCompanyService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/type-company")
public class TypeCompanyController {
    private final TypeCompanyService service;

    public TypeCompanyController(TypeCompanyService service) {
        this.service = service;
    }

    @GetMapping
    public List<TypeCompany> getAll() { return service.getAll(); }

    @GetMapping("/{id}")
    public TypeCompany getById(@PathVariable String id) { return service.getById(id); }

    @PostMapping
    public Object create(@RequestBody TypeCompany tc) {
    TypeCompany existed = service.getById(tc.getId());
    if (existed != null) {
        return ResponseEntity
            .status(409)
            .body("TypeCompany has existed with id: " + tc.getId());
    }
    return service.save(tc);
    }

    @PutMapping("/{id}")
    public TypeCompany update(@PathVariable String id, @RequestBody TypeCompany tc) {
        tc.setId(id);
        return service.save(tc);
    }

      @DeleteMapping("/{id}")
public Object delete(@PathVariable String id) {
    TypeCompany existed = service.getById(id);
    if (existed == null) {
        return ResponseEntity
            .status(404)
            .body("No TypeCompany exists to delete with id: " + id);
    }
    service.delete(id);
    return ResponseEntity.ok("Successfully deleted TypeCompany with id: " + id);
}
}
