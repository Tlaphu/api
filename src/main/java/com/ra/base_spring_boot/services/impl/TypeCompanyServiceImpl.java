package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormTypeCompany;
import com.ra.base_spring_boot.dto.resp.FormTypeCompanyResponse;
import com.ra.base_spring_boot.model.TypeCompany;
import com.ra.base_spring_boot.repository.ITypeCompanyRepository;
import com.ra.base_spring_boot.services.ITypeCompanyService; // Import interface
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Đã triển khai ITypeCompanyService
public class TypeCompanyServiceImpl implements ITypeCompanyService {

    private final ITypeCompanyRepository repository;

    private FormTypeCompanyResponse mapToResponse(TypeCompany model) {
        if (model == null) return null;
        return FormTypeCompanyResponse.builder()
                .id(model.getId())
                .name(model.getName())
                .createdAt(model.getCreated_at())
                .updatedAt(model.getUpdated_at())
                .build();
    }

    // --- Implementations ---

    @Override
    public List<FormTypeCompanyResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FormTypeCompanyResponse getById(String id) {
        Optional<TypeCompany> typeCompany = repository.findById(id);
        return typeCompany.map(this::mapToResponse)
                         .orElse(null);
    }

    @Override
    public FormTypeCompanyResponse create(FormTypeCompany form, String creatorEmail) {
        if (repository.existsById(form.getId())) {
             throw new RuntimeException("TypeCompany ID đã tồn tại: " + form.getId());
        }
        
        TypeCompany newType = TypeCompany.builder()
                .id(form.getId())
                .name(form.getName())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
        
        TypeCompany saved = repository.save(newType);
        return mapToResponse(saved);
    }

    @Override
    public FormTypeCompanyResponse update(String id, FormTypeCompany form, String updaterEmail) {
        TypeCompany existingType = repository.findById(id)
                .orElse(null);
        
        if (existingType == null) {
            return null;
        }
        
        existingType.setName(form.getName());
        existingType.setUpdated_at(new Date());
        
        TypeCompany updated = repository.save(existingType);
        return mapToResponse(updated);
    }

    @Override
    public boolean delete(String id, String deleterEmail) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}