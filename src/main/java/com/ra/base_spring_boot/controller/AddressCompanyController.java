package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;
import com.ra.base_spring_boot.services.IAddressCompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/address-companies")
@RequiredArgsConstructor
public class AddressCompanyController {

    private final IAddressCompanyService service;

    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @GetMapping
    public ResponseEntity<List<AddressCompanyResponse>> getAllForCompany(Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(service.getByCompanyEmail(email));
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @PostMapping
    public ResponseEntity<AddressCompanyResponse> create(@RequestBody FormAddressCompany form,
            Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(service.createForCompany(email, form));
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<AddressCompanyResponse> update(@PathVariable Long id,
            @RequestBody FormAddressCompany form,
            Principal principal) {
        String email = principal.getName();
        return ResponseEntity.ok(service.updateForCompany(email, id, form));
    }

    @PreAuthorize("hasAuthority('ROLE_COMPANY')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id, Principal principal) {
        String email = principal.getName();
        service.deleteForCompany(email, id);
        return ResponseEntity.noContent().build();
    }
}
