package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormTypeCompany;
import com.ra.base_spring_boot.dto.resp.FormTypeCompanyResponse;

import java.util.List;

public interface ITypeCompanyService {
    
    List<FormTypeCompanyResponse> getAll();
    
    FormTypeCompanyResponse getById(Long id); 
    
    FormTypeCompanyResponse create(FormTypeCompany form, String creatorEmail);
    
    FormTypeCompanyResponse update(Long id, FormTypeCompany form, String updaterEmail); 
    
    boolean delete(Long id, String deleterEmail); 
}