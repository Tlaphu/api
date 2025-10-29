package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyCompanyDetails;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.EmailService; 
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final EmailService emailService; 
    
   

    public CompanyAuthServiceImpl(
            IRoleService roleService,
            IAccountCompanyRepository accountCompanyRepository,
            ICompanyRepository companyRepository,
            IAddressCompanyRepository addressCompanyRepository,
            PasswordEncoder passwordEncoder,
            ITypeCompanyRepository typeCompanyRepository,
            @Qualifier("companyAuthManager") AuthenticationManager companyAuthManager,
            JwtProvider jwtProvider,
            EmailService emailService 
    ) {
        this.roleService = roleService;
        this.accountCompanyRepository = accountCompanyRepository;
        this.companyRepository = companyRepository;
        this.addressCompanyRepository = addressCompanyRepository;
        this.passwordEncoder = passwordEncoder;
        this.typeCompanyRepository = typeCompanyRepository;
        this.companyAuthManager = companyAuthManager;
        this.jwtProvider = jwtProvider;
        this.emailService = emailService; 
    }

    @Override
    public void register(FormRegisterCompany form) {
        if (accountCompanyRepository.existsByEmail(form.getEmail())) {
            throw new HttpBadRequest("Email is already registered");
        }

        

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_COMPANY));

        Optional<Company> existingCompanyOpt = companyRepository.findByEmail(form.getCompanyEmail());
        Company company;

        if (existingCompanyOpt.isPresent()) {
            company = existingCompanyOpt.get();
        } else {
            company = Company.builder()
                    .name(form.getCompanyName())
                    .email(form.getCompanyEmail())
                    .phone(form.getPhone())
                    .created_at(new Date())
                    .updated_at(new Date())
                    .build();
            companyRepository.save(company);
        }
        
        
        AccountCompany accountCompany = AccountCompany.builder()
                .fullName(form.getFullName())
                .email(form.getEmail())
               
                .password(passwordEncoder.encode(""))
                .roles(roles)
                .company(company)
                .status(false) 
                .build();

        accountCompanyRepository.save(accountCompany); 

        AddressCompany address = AddressCompany.builder()
                .company(company)
                .address(form.getAddress())
                .build();
        addressCompanyRepository.save(address);
        
        System.out.println("✅ New Company Account registered successfully, pending Admin approval: " + form.getEmail());
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
        

        if (!accountCompany.isStatus()) {
            
            throw new HttpBadRequest("Account is not activated. Please wait for Admin approval.");
        }
        
        Company company = accountCompany.getCompany();

        Set<String> roles = companyDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateCompanyToken(accountCompany, roles))
                .accountCompany(toAccountResponse(accountCompany))
                .roles(roles)
                .build();
    }

       @Override
        public void logout(String token) {
            System.out.println("Logout token: " + token);
        }
    
        @Override
        public void activateAccount(String token) {
            AccountCompany accountCompany = accountCompanyRepository.findByResetToken(token)
                    .orElseThrow(() -> new HttpBadRequest("Invalid activation token."));
            
            accountCompany.setStatus(true);
            accountCompany.setResetToken(null);
            accountCompanyRepository.save(accountCompany);
        }


    @Override
    public String forgotPassword(FormForgotPassword form) {
        AccountCompany accountCompany = accountCompanyRepository
            .findByEmail(form.getEmail()) 
            .orElseThrow(() -> new HttpBadRequest("Account not found with this email."));
        
        String resetToken = UUID.randomUUID().toString();
        accountCompany.setResetToken(resetToken); 
        accountCompanyRepository.save(accountCompany); 

       
        String frontendBaseUrl = "http://localhost:5173"; 
    
   
        String resetLink = frontendBaseUrl + "/reset-password?token=" + resetToken;

        
        emailService.sendResetPasswordEmail(
            accountCompany.getEmail(), 
            accountCompany.getFullName(), 
            resetLink
        );

        
        return "Password reset link sent to email.";
    }

    @Override
    public void resetPassword(FormResetPassword form) {
    
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            throw new HttpBadRequest("New passwords do not match.");
        }
        
        
        AccountCompany accountCompany = accountCompanyRepository.findByResetToken(form.getToken())
                .orElseThrow(() -> new HttpBadRequest("Invalid or expired reset token."));

        
        accountCompany.setPassword(passwordEncoder.encode(form.getNewPassword()));
        accountCompany.setResetToken(null); 
        accountCompanyRepository.save(accountCompany);
    }
    
    @Override
    public void changePassword(FormChangePassword form) {
        AccountCompany accountCompany = jwtProvider.getCurrentAccountCompany();
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

        Company company = accountCompany.getCompany();
        company.setName(form.getName());
        company.setPhone(form.getPhone());
        company.setWebsite(form.getWebsite());
        company.setLogo(form.getLogo());
        company.setLink_fb(form.getLinkFb());
        company.setLink_linkedin(form.getLinkLinkedin());
        company.setDescription(form.getDescription());
        company.setCompanyPolicy(form.getCompanyPolicy());
        company.setUpdated_at(new Date());

        companyRepository.save(company);
    }


    @Override
    public List<CompanyResponse> findTop20ByFollower() {
        Pageable topTwenty = PageRequest.of(0, 20);
        List<Company> companies = companyRepository.findAllByOrderByFollowerDesc(topTwenty);

        return companies.stream()
                .map(this::toCompanyResponse)
                .collect(Collectors.toList());
    }



    @Override
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }


    @Override
    public CompanyResponse findById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Company not found"));
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
                .updated_at(new Date()) 
                .typeCompanyName(company.getTypeCompany() != null ? company.getTypeCompany().getName() : null)
                .addresses(addresses)
                .build();
    }

    private AccountCompanyResponse toAccountResponse(AccountCompany accountCompany) {
        Company company = accountCompany.getCompany();

        return AccountCompanyResponse.builder()
                .id(accountCompany.getId())
                .email(accountCompany.getEmail())
                .fullName(accountCompany.getFullName())
                .phone(company.getPhone())
                .status(accountCompany.isStatus())
                .company(CompanyResponse.builder()
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
                        .build())
                .build();
    }
    
    private CompanyResponse toCompanyResponse(Company company) {
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
                .typeCompanyName(
                        company.getTypeCompany() != null ? company.getTypeCompany().getName() : null
                )
                .build();
    }
}