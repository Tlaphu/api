package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;

import java.util.List;

public interface IAddressCompanyService {
    List<AddressCompanyResponse> getByCompanyEmail(String email);
    AddressCompanyResponse createForCompany(String email, FormAddressCompany form);
    AddressCompanyResponse updateForCompany(String email, Long id, FormAddressCompany form);
    void deleteForCompany(String email, Long id);
}
