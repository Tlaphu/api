package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.AddressCompany;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.model.Location;
import com.ra.base_spring_boot.model.AccountCompany;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import com.ra.base_spring_boot.repository.IAddressCompanyRepository;
import com.ra.base_spring_boot.repository.ILocationRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
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
    private final IAccountCompanyRepository accountCompanyRepository;
    private final ILocationRepository locationRepository;
    private final JwtProvider jwtProvider;

    @Override
    public List<AddressCompanyResponse> getAllForCurrentCompany() {
        String email = jwtProvider.getCompanyUsername();
        if (email == null) {
            throw new HttpBadRequest("Unauthorized: company not found");
        }

        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Company " + email + " not found"));
        Company company = accountCompany.getCompany();

        return addressRepository.findByCompany(company)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AddressCompanyResponse create(FormAddressCompany form) {
        String email = jwtProvider.getCompanyUsername();
        if (email == null) {
            throw new HttpBadRequest("Unauthorized: company not found");
        }

        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Company " + email + " not found"));
        Company company = accountCompany.getCompany();

        Location location = locationRepository.findById(form.getLocationId())
                .orElseThrow(() -> new HttpBadRequest("Location not found with id: " + form.getLocationId()));

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
    public AddressCompanyResponse update(Long id, FormAddressCompany form) {
        String email = jwtProvider.getCompanyUsername();
        if (email == null) {
            throw new HttpBadRequest("Unauthorized: company not found");
        }

        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Company " + email + " not found"));
        Company company = accountCompany.getCompany();

        AddressCompany address = addressRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Address not found with id: " + id));

        if (!address.getCompany().getId().equals(company.getId())) {
            throw new HttpBadRequest("You do not have permission to update this address");
        }

        Location location = locationRepository.findById(form.getLocationId())
                .orElseThrow(() -> new HttpBadRequest("Location not found with id: " + form.getLocationId()));

        address.setAddress(form.getAddress());
        address.setMap_url(form.getMapUrl());
        address.setLocation(location);

        addressRepository.save(address);
        return toResponse(address);
    }

    @Override
    public void delete(Long id) {
        String email = jwtProvider.getCompanyUsername();
        if (email == null) {
            throw new HttpBadRequest("Unauthorized: company not found");
        }

        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Company " + email + " not found"));
        Company company = accountCompany.getCompany();

        AddressCompany address = addressRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Address not found with id: " + id));

        if (!address.getCompany().getId().equals(company.getId())) {
            throw new HttpBadRequest("You do not have permission to delete this address");
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
