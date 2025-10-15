package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;
import com.ra.base_spring_boot.model.AddressCompany;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.repository.IAddressCompanyRepository;
import com.ra.base_spring_boot.services.IAddressCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressCompanyServiceImpl implements IAddressCompanyService {

    private final IAddressCompanyRepository addressRepository;
    private final IAccountCompanyRepository companyRepository;
    private final ILocationRepository locationRepository;

    @Override
    public List<AddressCompanyResponse> getByCompanyEmail(String email) {
        var accountCompany = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company" + email + " not found"));
        Company company = accountCompany.getCompany();

        return addressRepository.findByCompany(company)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AddressCompanyResponse createForCompany(String email, FormAddressCompany form) {
        var accountCompany = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company" + email + " not found"));
        Company company = accountCompany.getCompany();

        Location location = locationRepository.findById(form.getLocationId())
                .orElseThrow(() -> new RuntimeException("Cannot found company location" + form.getLocationId()));

        AddressCompany address = AddressCompany.builder()
                .company(company)
                .address(form.getAddress())
                .map_url(form.getMapUrl())
                .location(location)
                .build();

        addressRepository.save(address);
        return toResponse(address);
    }

    @Override
    public AddressCompanyResponse updateForCompany(String email, Long id, FormAddressCompany form) {
        var accountCompany = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company" + email + " not found"));
        Company company = accountCompany.getCompany();

        AddressCompany address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot found location with id :" + id));

        if (!address.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("You cannot change");
        }

        Location location = locationRepository.findById(form.getLocationId())
                .orElseThrow(() -> new RuntimeException("Cannot found company location "  + form.getLocationId()));

        address.setAddress(form.getAddress());
        address.setMap_url(form.getMapUrl());
        address.setLocation(location);

        addressRepository.save(address);
        return toResponse(address);
    }

    @Override
    public void deleteForCompany(String email, Long id) {
        var accountCompany = companyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Company" + email + " not found"));
        Company company = accountCompany.getCompany();

        AddressCompany address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot found location with id :" + id));

        if (!address.getCompany().getId().equals(company.getId())) {
            throw new RuntimeException("You");
        }

        addressRepository.delete(address);
    }

    private AddressCompanyResponse toResponse(AddressCompany entity) {
        return AddressCompanyResponse.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .mapUrl(entity.getMap_url())
                .locationName(entity.getLocation() != null ? entity.getLocation().getName() : null)
                .build();
    }
}

