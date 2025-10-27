package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.resp.AddressCompanyResponse;
import com.ra.base_spring_boot.dto.resp.CompanyResponse;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Admin;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import com.ra.base_spring_boot.repository.IAddressCompanyRepository;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyAdminDetails;
import com.ra.base_spring_boot.services.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements IAdminService {
    private final AuthenticationManager adminAuthManager;
    private final JwtProvider jwtProvider;
    private final ICompanyRepository companyRepository;
    private final IAccountCompanyRepository accountCompanyRepository;
    private final IAddressCompanyRepository addressCompanyRepository;

    public AdminServiceImpl(
            @Qualifier("adminAuthManager") AuthenticationManager adminAuthManager,
            JwtProvider jwtProvider,
            ICompanyRepository companyRepository,
            IAccountCompanyRepository accountCompanyRepository,
            IAddressCompanyRepository addressCompanyRepository
    ) {
        this.adminAuthManager = adminAuthManager;
        this.jwtProvider = jwtProvider;
        this.companyRepository = companyRepository;
        this.accountCompanyRepository = accountCompanyRepository;
        this.addressCompanyRepository = addressCompanyRepository;
    }

    @Override
    public JwtResponse login(FormLogin formLogin) {
        try {
            Authentication authentication = adminAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );

            MyAdminDetails adminDetails = (MyAdminDetails) authentication.getPrincipal();
            Admin admin = adminDetails.getAdmin();

            Set<String> roles = adminDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            String token = jwtProvider.generateAdminToken(admin, roles);

            return JwtResponse.builder()
                    .accessToken(token)
                    .admin(admin)
                    .roles(roles)
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
            throw new HttpBadRequest("Wrong email");
        }
    }

    @Override
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Company not found"));

        addressCompanyRepository.deleteAll(addressCompanyRepository.findByCompany(company));
        accountCompanyRepository.deleteAll(accountCompanyRepository.findAllByCompany_Id(company.getId()));

        companyRepository.delete(company);
    }

    @Override
    public CompanyResponse updateCompany(Long id, FormUpdateCompany form) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Company not found"));

        company.setName(form.getName());
        company.setPhone(form.getPhone());
        company.setWebsite(form.getWebsite());
        company.setLogo(form.getLogo());
        company.setLink_fb(form.getLinkFb());
        company.setLink_linkedin(form.getLinkLinkedin());
        company.setDescription(form.getDescription());
        company.setCompanyPolicy(form.getCompanyPolicy());
        company.setUpdated_at(new java.util.Date());

        companyRepository.save(company);
        return toResponse(company);
    }

    private CompanyResponse toResponse(Company company) {
        List<AddressCompanyResponse> addresses = addressCompanyRepository.findByCompany(company)
                .stream()
                .map(addr -> AddressCompanyResponse.builder()
                        .id(addr.getId())
                        .address(addr.getAddress())
                        .mapUrl(addr.getMap_url())
                        .locationName(addr.getLocation() != null ? addr.getLocation().getName() : null)
                        .build())
                .collect(Collectors.toList());

        return CompanyResponse.builder()
                .id(company.getId())
                .name(company.getName())
                .email(company.getEmail())
                .phone(company.getPhone())
                .logo(company.getLogo())
                .website(company.getWebsite())
                .link_fb(company.getLink_fb())
                .link_linkedin(company.getLink_linkedin())
                .follower(company.getFollower())
                .size(company.getSize())
                .description(company.getDescription())
                .CompanyPolicy(company.getCompanyPolicy())
                .created_at(company.getCreated_at())
                .updated_at(company.getUpdated_at())
                .typeCompanyName(company.getTypeCompany() != null ? company.getTypeCompany().getName() : null)
                .addresses(addresses)
                .build();
    }

}
