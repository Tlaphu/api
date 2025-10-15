package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormTypeCompany;
import com.ra.base_spring_boot.dto.resp.FormTypeCompanyResponse;

import java.util.List;

public interface ITypeCompanyService {
    
    List<FormTypeCompanyResponse> getAll();
    
    FormTypeCompanyResponse getById(String id);
    
    FormTypeCompanyResponse create(FormTypeCompany form, String creatorEmail);
    
    FormTypeCompanyResponse update(String id, FormTypeCompany form, String updaterEmail);
    
    boolean delete(String id, String deleterEmail);
}