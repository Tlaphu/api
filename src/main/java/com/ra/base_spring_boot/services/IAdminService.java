package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.resp.CompanyResponse;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

import java.util.List;

public interface IAdminService {
    JwtResponse login(FormLogin formLogin);

    List<CompanyResponse> findAll();

    void deleteCompany(Long id);

    CompanyResponse updateCompany(Long id, FormUpdateCompany form);
}
