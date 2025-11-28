package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyAdminDetails;
import com.ra.base_spring_boot.services.IAdminService;
import com.ra.base_spring_boot.services.EmailService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder; // Đã thêm
import org.springframework.stereotype.Service;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements IAdminService {


    private static final String DEFAULT_PASSWORD = "Welcome123!";

    private final AuthenticationManager adminAuthManager;
    private final JwtProvider jwtProvider;
    private final ICompanyRepository companyRepository;
    private final IAccountCompanyRepository accountCompanyRepository;
    private final IAddressCompanyRepository addressCompanyRepository;
    private final ICandidateRepository candidateRepository;
    private final ILocationRepository locationRepository;


    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(
            @Qualifier("adminAuthManager") AuthenticationManager adminAuthManager,
            JwtProvider jwtProvider,
            ICompanyRepository companyRepository,
            IAccountCompanyRepository accountCompanyRepository,
            IAddressCompanyRepository addressCompanyRepository,
            ICandidateRepository candidateRepository, ILocationRepository locationRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.adminAuthManager = adminAuthManager;
        this.jwtProvider = jwtProvider;
        this.companyRepository = companyRepository;
        this.accountCompanyRepository = accountCompanyRepository;
        this.addressCompanyRepository = addressCompanyRepository;
        this.candidateRepository = candidateRepository;
        this.locationRepository = locationRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
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
    public boolean activateCompanyAccount(Long id) { // 👈 Sửa từ 'void' thành 'boolean'

        // 1. Tìm tài khoản và xử lý lỗi
        AccountCompany account = accountCompanyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Company Account not found"));

        boolean wasActive = account.isStatus();
        boolean newStatus = !wasActive; // Giá trị cần trả về
        account.setStatus(newStatus);

        // --- Xử lý khi TẮT (Deactivate) ---
        if (!newStatus) {
            // newStatus = false

            // Khi tài khoản bị TẮT, xóa token để đảm bảo người dùng không thể đăng nhập.
            account.setVerificationToken(null);
            accountCompanyRepository.save(account);

            return newStatus; // 👈 Trả về false
        }

        // --- Xử lý khi BẬT (Activate) (newStatus = true) ---

        // Đảm bảo token xác minh được xóa khi tài khoản được kích hoạt
        account.setVerificationToken(null);

        // Kiểm tra: Đây có phải là lần kích hoạt đầu tiên và cần gửi mật khẩu mặc định?
        if (account.getPassword() == null) {

            // Trường hợp LẦN ĐẦU: Đặt mật khẩu mặc định và gửi email
            account.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));

            accountCompanyRepository.save(account);

            // Gửi email chứa thông tin đăng nhập mặc định
            emailService.sendLoginCredentialsEmail(
                    account.getEmail(),
                    account.getFullName(),
                    DEFAULT_PASSWORD
            );

        } else {

            // Trường hợp LẦN 2 trở lên: Giữ lại mật khẩu cũ.
            accountCompanyRepository.save(account);

            // KHÔNG gửi email mật khẩu mặc định.
        }

        return newStatus; // 👈 Trả về true (vì newStatus = true)
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

    @Override
    public List<CandidateResponse> findAllCandidates() {
        return candidateRepository.findAll()
                .stream()
                .map(this::mapCandidateToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CandidateResponse updateCandidate(Long id, FormUpdateProfile form) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));

        candidate.setName(form.getName());
        candidate.setEmail(form.getEmail());
        candidate.setPhone(form.getPhone());
        candidate.setAddress(form.getAddress());
        candidate.setGender(form.getGender());
        candidate.setDob(form.getDob());
        candidate.setLink(form.getLink());
        candidate.setUpdated_at(new Date());
        candidateRepository.save(candidate);

        return mapCandidateToResponse(candidate);
    }

    @Override
    public void deleteCandidate(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));
        candidateRepository.delete(candidate);
    }


    @Override
    public List<AccountCompanyResponse> findAllAccountsCompany() {
        return accountCompanyRepository.findAll()
                .stream()
                .map(account -> AccountCompanyResponse.builder()
                        .id(account.getId())
                        .email(account.getEmail())
                        .fullName(account.getFullName())
                        .status(account.isStatus())
                        .gender(account.getGender())
                        .phone(account.getPhone())
                        .dob(account.getDob())
                        .isPremium(account.isPremium())
                        .premiumUntil(account.getPremiumUntil())
                        .company(
                                CompanyResponse.builder()
                                        .id(account.getCompany().getId())
                                        .name(account.getCompany().getName())
                                        .email(account.getCompany().getEmail())
                                        .phone(account.getCompany().getPhone())
                                        .link_Github(account.getCompany().getLink_Github())
                                        .link_fb(account.getCompany().getLink_fb())
                                        .link_linkedin(account.getCompany().getLink_linkedin())
                                        .logo(account.getCompany().getLogo())

                                        .build()
                        )
                        .build()
                ).collect(Collectors.toList());
    }

    @Override
    public void deleteAccountCompany(Long id) {
        accountCompanyRepository.deleteById(id);
    }


    private CandidateResponse mapCandidateToResponse(Candidate candidate) {
        return CandidateResponse.builder()
                .id(candidate.getId())
                .name(candidate.getName())
                .email(candidate.getEmail())
                .phone(candidate.getPhone())
                .address(candidate.getAddress())
                .gender(candidate.getGender())
                .dob(candidate.getDob())
                .link(candidate.getLink())
                .status(candidate.isStatus())
                .logo(candidate.getLogo())
                .isOpen(candidate.getIsOpen())
                .isPremium(candidate.isPremium())
                .premiumUntil(candidate.getPremiumUntil())
                .Title(candidate.getTitle())
                .description(candidate.getDescription())
                .experience(candidate.getExperience())
                .development(candidate.getDevelopment())
                .skills(candidate.getSkillCandidates() == null ? null
                        : candidate.getSkillCandidates().stream()
                        .map(s -> SkillsCandidateResponse.builder()
                                .id(s.getId())
                                .skillName(s.getSkill() != null ? s.getSkill().getName() : null)
                                .levelJobName(s.getLevelJob() != null ? s.getLevelJob().getName() : null)
                                .createdAt(s.getCreatedAt())
                                .updatedAt(s.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .educations(candidate.getEducationCandidates() == null ? null
                        : candidate.getEducationCandidates().stream()
                        .<EducationCandidateResponse>map(e -> EducationCandidateResponse.builder()
                                .id(e.getId())
                                .nameEducation(e.getNameEducation())
                                .major(e.getMajor())
                                .gpa(e.getGpa())
                                .startedAt((e.getStartedAt()))
                                .endAt((e.getEndAt()))
                                .info(e.getInfo())
                                .build())
                        .collect(Collectors.toList()))
                .experiences(candidate.getExperienceCandidates() == null ? null
                        : candidate.getExperienceCandidates().stream()
                        .map(ex -> ExperienceCandidateResponse.builder()
                                .id(ex.getId())
                                .company(ex.getCompany())
                                .position(ex.getPosition())
                                .started_at((ex.getStarted_at()))
                                .end_at((ex.getEnd_at()))
                                .info(ex.getInfo())
                                .build())
                        .collect(Collectors.toList()))
                .certificates(candidate.getCertificateCandidates() == null ? null
                        : candidate.getCertificateCandidates().stream()
                        .map(c -> CertificateCandidateResponse.builder()
                                .id(c.getId())
                                .name(c.getName())
                                .organization(c.getOrganization())
                                .started_at((c.getStarted_at()))
                                .end_at((c.getEnd_at()))
                                .info(c.getInfo())
                                .build())
                        .collect(Collectors.toList()))
                .project(candidate.getProjectCandidates() == null ? null :
                        candidate.getProjectCandidates().stream()
                                .map(p -> ProjectCandidateResponse.builder()
                                        .id(p.getId())
                                        .name(p.getName())
                                        .link(p.getLink())
                                        .started_at(p.getStarted_at())
                                        .end_at(p.getEnd_at())
                                        .info(p.getInfo())
                                        .build())
                                .collect(Collectors.toList()))
                .build();
    }
    @Override
    public List<AddressCompanyResponse> getAllByCompanyId(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new HttpBadRequest("Company not found with id: " + companyId));

        return addressCompanyRepository.findByCompany(company)
                .stream()
                .map(this::toAddressResponse)
                .toList();
    }

    @Override
    public AddressCompanyResponse create(Long companyId, FormAddressCompany form) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new HttpBadRequest("Company not found with id: " + companyId));

        Location location = locationRepository.findById(form.getLocationId())
                .orElseThrow(() -> new HttpBadRequest("Location not found with id: " + form.getLocationId()));

        AddressCompany address = AddressCompany.builder()
                .company(company)
                .address(form.getAddress())
                .map_url(form.getMapUrl())
                .location(location)
                .build();

        addressCompanyRepository.save(address);
        return toAddressResponse(address);
    }

    @Override
    public AddressCompanyResponse update(Long id, FormAddressCompany form) {
        AddressCompany address = addressCompanyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Address not found with id: " + id));

        if (form.getLocationId() != null) {
            Location location = locationRepository.findById(form.getLocationId())
                    .orElseThrow(() -> new HttpBadRequest("Location not found with id: " + form.getLocationId()));
            address.setLocation(location);
        }

        address.setAddress(form.getAddress());
        address.setMap_url(form.getMapUrl());

        addressCompanyRepository.save(address);
        return toAddressResponse(address);
    }

    @Override
    public void delete(Long id) {
        AddressCompany address = addressCompanyRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Address not found with id: " + id));

        addressCompanyRepository.delete(address);
    }

    private AddressCompanyResponse toAddressResponse(AddressCompany entity) {
        return AddressCompanyResponse.builder()
                .id(entity.getId())
                .address(entity.getAddress())
                .mapUrl(entity.getMap_url())
                .locationName(entity.getLocation() != null ? entity.getLocation().getName() : null)
                .build();
    }
}
