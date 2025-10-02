package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.model.TypeCompany;
import com.ra.base_spring_boot.repository.TypeCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeCompanyService {
    private final TypeCompanyRepository repo;

    public TypeCompanyService(TypeCompanyRepository repo) {
        this.repo = repo;
    }

    public List<TypeCompany> getAll() { return repo.findAll(); }
    public TypeCompany getById(String id) { return repo.findById(id).orElse(null); }
    public TypeCompany save(TypeCompany tc) { return repo.save(tc); }
    public void delete(String id) { repo.deleteById(id); }
}