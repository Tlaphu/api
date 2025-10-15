package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import com.ra.base_spring_boot.repository.IAddressCompanyRepository;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.repository.ITypeCompanyRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyCompanyDetails;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;    
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompanyAuthServiceImpl implements ICompanyAuthService {

    private final IRoleService roleService;
    private final IAccountCompanyRepository accountCompanyRepository;
    private final ICompanyRepository companyRepository;
    private final IAddressCompanyRepository addressCompanyRepository;
    private final PasswordEncoder passwordEncoder;
    private final ITypeCompanyRepository typeCompanyRepository;
    @Qualifier("companyAuthManager")
    private final AuthenticationManager companyAuthManager;
    private final JwtProvider jwtProvider;

    public CompanyAuthServiceImpl(
            IRoleService roleService,
            IAccountCompanyRepository accountCompanyRepository, ICompanyRepository companyRepository, IAddressCompanyRepository addressCompanyRepository,
            PasswordEncoder passwordEncoder,
            ITypeCompanyRepository typeCompanyRepository,
            @Qualifier("companyAuthManager") AuthenticationManager companyAuthManager,
            JwtProvider jwtProvider
    ) {
        this.roleService = roleService;
        this.accountCompanyRepository = accountCompanyRepository;
        this.companyRepository = companyRepository;
        this.addressCompanyRepository = addressCompanyRepository;
        this.passwordEncoder = passwordEncoder;
        this.companyAuthManager = companyAuthManager;
        this.typeCompanyRepository = typeCompanyRepository;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void register(FormRegisterCompany form) {
        if (accountCompanyRepository.existsByEmail(form.getEmail())) {
            throw new HttpBadRequest("Email is already registered");
        }

        if (!form.getPassword().equals(form.getConfirmPassword())) {
            throw new HttpBadRequest("Passwords do not match");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_COMPANY));


        AccountCompany accountCompany = AccountCompany.builder()
                .id(UUID.randomUUID().toString())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .roles(roles)
                .build();

        TypeCompany typeCompany = null;
        if (form.getTypeCompanyId() != null) {
            typeCompany = typeCompanyRepository.findById(form.getTypeCompanyId())
                    .orElseThrow(() -> new HttpBadRequest("Invalid typeCompanyId"));
        }
        Company company = Company.builder()
                .id(UUID.randomUUID().toString())
                .name(form.getName())
                .phone(form.getPhone())
                .logo(form.getLogo())
                .website(form.getWebsite())
                .link_fb(form.getLinkFb())
                .link_linkedin(form.getLinkLinkedin())
                .description(form.getDescription())
                .size(form.getSize())
                .follower(form.getFollower())
                .typeCompany(typeCompany)
                .accountCompany(accountCompany)
                .created_at(new Date())
                .updated_at(new Date())
                .build();

        accountCompany.setCompany(company);

        accountCompanyRepository.save(accountCompany);

        if (form.getAddress() != null) {
            FormAddressCompany addr = form.getAddress();

            AddressCompany addressCompany = AddressCompany.builder()
                    .id(UUID.randomUUID().toString())
                    .company(company)
                    .address(addr.getAddress())
                    .map_url(addr.getMapUrl())
                    .build();

            addressCompanyRepository.save(addressCompany);
        }
    }



    @Override
    public JwtResponse login(FormLogin formLogin) {
        Authentication authentication;
        try {
            authentication = companyAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new HttpBadRequest("Email or password is incorrect");
        }

        MyCompanyDetails companyDetails = (MyCompanyDetails) authentication.getPrincipal();
        AccountCompany accountCompany = companyDetails.getAccountCompany();

        Set<String> roles = companyDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Company company = accountCompany.getCompany();

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateCompanyToken(accountCompany, roles))
                .company(company)
                .roles(roles)
                .build();
    }

    @Override
    public void logout(String token) {
        System.out.println("Logout token: " + token);
    }

    @Override
    public String forgotPassword(FormForgotPassword form) {
        AccountCompany accountCompany = accountCompanyRepository
                .findByEmailAndRoles_RoleName(form.getEmail(), form.getRole())
                .orElseThrow(() -> new HttpBadRequest("Company account not found with email and role"));

        String resetToken = UUID.randomUUID().toString();
        System.out.println("Send reset password token to email: " + accountCompany.getEmail());
        return resetToken;
    }

    @Override
    public void changePassword(FormChangePassword form) {
        AccountCompany accountCompany = jwtProvider.getCurrentCompany();
        if (accountCompany == null) {
            throw new HttpBadRequest("Unauthorized: Company not found");
        }

        if (!passwordEncoder.matches(form.getOldPassword(), accountCompany.getPassword())) {
            throw new HttpBadRequest("Old password is incorrect");
        }

        accountCompany.setPassword(passwordEncoder.encode(form.getNewPassword()));
        accountCompanyRepository.save(accountCompany);
    }

    @Override
    public void updateProfile(FormUpdateCompany form) {
        String email = jwtProvider.getCompanyUsername();
        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Company not found"));

        accountCompanyRepository.save(accountCompany);
    }
    @Override
    public List<Company> findTop20ByFollower() {
       
        Pageable topTwenty = PageRequest.of(0, 20); 
        
        return companyRepository.findAllByOrderByFollowerDesc(topTwenty);
    }
}
