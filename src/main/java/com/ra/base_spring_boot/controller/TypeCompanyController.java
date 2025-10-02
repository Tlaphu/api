package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.TypeCompany;
import com.ra.base_spring_boot.services.TypeCompanyService;
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
    public TypeCompany create(@RequestBody TypeCompany tc) { return service.save(tc); }

    @PutMapping("/{id}")
    public TypeCompany update(@PathVariable String id, @RequestBody TypeCompany tc) {
        tc.setId(id);
        return service.save(tc);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) { service.delete(id); }
}