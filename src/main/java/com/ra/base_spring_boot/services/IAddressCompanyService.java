package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;

import java.util.List;

public interface IAddressCompanyService {
    List<AddressCompanyResponse> getAllForCurrentCompany();
    AddressCompanyResponse create( FormAddressCompany form);
    AddressCompanyResponse update( Long id, FormAddressCompany form);
    void delete(Long id);
}
