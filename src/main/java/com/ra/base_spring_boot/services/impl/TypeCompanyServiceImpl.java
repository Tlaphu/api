package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormTypeCompany;
import com.ra.base_spring_boot.dto.resp.FormTypeCompanyResponse;
import com.ra.base_spring_boot.model.TypeCompany;
import com.ra.base_spring_boot.repository.ITypeCompanyRepository;
import com.ra.base_spring_boot.services.ITypeCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

  

    @Override
    public List<FormTypeCompanyResponse> getAll() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FormTypeCompanyResponse getById(Long id) { 
        Optional<TypeCompany> typeCompany = repository.findById(id);
        return typeCompany.map(this::mapToResponse)
                             .orElse(null);
    }

    @Override
    public FormTypeCompanyResponse create(FormTypeCompany form, String creatorEmail) {
        
        
        TypeCompany newType = TypeCompany.builder()
                
                .name(form.getName())
                .created_at(new Date())
                .updated_at(new Date())
                .build();
        
        TypeCompany saved = repository.save(newType);
        return mapToResponse(saved);
    }

    @Override
    public FormTypeCompanyResponse update(Long id, FormTypeCompany form, String updaterEmail) { 
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
    public boolean delete(Long id, String deleterEmail) { 
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}