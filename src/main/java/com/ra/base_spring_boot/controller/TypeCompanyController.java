package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormTypeCompany;
import com.ra.base_spring_boot.dto.resp.FormTypeCompanyResponse; 
import com.ra.base_spring_boot.services.ITypeCompanyService; 
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/type-company")
@RequiredArgsConstructor
public class TypeCompanyController {

    private final ITypeCompanyService service;

    
    @GetMapping
    public ResponseEntity<List<FormTypeCompanyResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<FormTypeCompanyResponse> getById(@PathVariable Long id) { 
        FormTypeCompanyResponse response = service.getById(id);
        if (response == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(response);
    }


    
    @PreAuthorize("hasAuthority('ROLE_COMPANY')") 
    @PostMapping
    public ResponseEntity<FormTypeCompanyResponse> create(@RequestBody FormTypeCompany form, Principal principal) { 
      
        String creatorEmail = principal.getName();
        FormTypeCompanyResponse createdType = service.create(form, creatorEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdType);
    }


    
    @PreAuthorize("hasAuthority('ROLE_COMPANY')") 
    @PutMapping("/{id}")
    public ResponseEntity<FormTypeCompanyResponse> update(@PathVariable Long id, 
                                                        @RequestBody FormTypeCompany form,
                                                        Principal principal) {
        
        String updaterEmail = principal.getName();
        FormTypeCompanyResponse updatedType = service.update(id, form, updaterEmail);
        
        if (updatedType == null) {
             return ResponseEntity.notFound().build(); 
        }
        
        return ResponseEntity.ok(updatedType); 
    }


    
    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) { 
        
        String deleterEmail = principal.getName();
        boolean deleted = service.delete(id, deleterEmail);
        
        if (!deleted) {
            return ResponseEntity.notFound().build(); 
        }
        
        return ResponseEntity.noContent().build();
    }
}