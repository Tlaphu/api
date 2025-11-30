package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.event.NotificationEvent;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.*;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyCompanyDetails;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.EmailService;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.ra.base_spring_boot.model.AccountCompany;
import org.springframework.transaction.annotation.Transactional;

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
    private final ICandidateRepository candidateRepository;
    private final JobRepository jobRepository;
    private final ApplicationEventPublisher eventPublisher;



    public CompanyAuthServiceImpl(
            IRoleService roleService,
            IAccountCompanyRepository accountCompanyRepository,
            ICompanyRepository companyRepository,
            IAddressCompanyRepository addressCompanyRepository,
            PasswordEncoder passwordEncoder,
            ITypeCompanyRepository typeCompanyRepository,
            @Qualifier("companyAuthManager") AuthenticationManager companyAuthManager,
            JwtProvider jwtProvider,
            EmailService emailService, ICandidateRepository candidateRepository, JobRepository jobRepository, ApplicationEventPublisher eventPublisher
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
        this.candidateRepository = candidateRepository;
        this.jobRepository = jobRepository;
        this.eventPublisher = eventPublisher;
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

                .password(null)
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

            eventPublisher.publishEvent(new NotificationEvent(
                    this,
                    "Đăng ký công ty mới",
                    "Công ty " + company.getName() + " vừa đăng ký tài khoản và đang chờ duyệt.",
                    "NEW_COMPANY_REGISTER",
                    1L,        // receiverId
                    "ADMIN",              // receiverType
                    "/admin/company-verify/" + company.getId(),  // link để admin duyệt
                    "SYSTEM",             // senderType
                    null                  // senderId
            ));


        System.out.println("✅ New Company Account registered successfully, pending Admin approval: " + form.getEmail());
    }
    @Override
    public AccountCompany getCurrentAccountCompany() {

        return jwtProvider.getCurrentAccountCompany();
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
    @Transactional
    public AccountCompanyResponse getCurrentCompanyInfo() {
        AccountCompany accountCompany = jwtProvider.getCurrentAccountCompany();
        if (accountCompany == null) {
            throw new HttpBadRequest("Unauthorized: No company account found in token");
        }

        AccountCompany fullCompany = accountCompanyRepository.findById(accountCompany.getId())
                .orElseThrow(() -> new HttpBadRequest("Company not found"));
        fullCompany.getCompany().getAddresses().size();
        return toAccountResponse(fullCompany);
    }


    @Override
    public String forgotPassword(FormForgotPassword form) {
        AccountCompany accountCompany = accountCompanyRepository
                .findByEmail(form.getEmail())
                .orElseThrow(() -> new HttpBadRequest("Account not found with this email."));
        if (!accountCompany.isStatus()) {
            throw new HttpBadRequest("Account is not activated.");
        }
        String resetToken = UUID.randomUUID().toString();
        accountCompany.setResetToken(resetToken);
        accountCompanyRepository.save(accountCompany);

        String frontendBaseUrl = "http://localhost:5173";

        String resetLink = frontendBaseUrl + "/reset-password?token=" + resetToken + "&role=COMPANY";

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
        company.setLink_Github(form.getLink_Github());
        company.setDescription(form.getDescription());
        company.setCompanyPolicy(form.getCompanyPolicy());
        company.setUpdated_at(new Date());
        company.setSize(form.getSize());

        companyRepository.save(company);
    }

    @Override
    @Transactional
    public List<CompanyResponse> findTop20ByFollower() {
        Pageable topTwenty = PageRequest.of(0, 20);
        List<Company> companies = companyRepository.findAllByOrderByFollowerDesc(topTwenty);

        return companies.stream()
                .map(company -> CompanyResponse.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .email(company.getEmail())
                        .description(company.getDescription())
                        .logo(company.getLogo())
                        .follower(company.getFollower())
                        .totalJobs(companyRepository.countJobsByCompanyId(company.getId()))
                        .addresses(company.getAddresses().stream()
                                .map(address -> AddressCompanyResponse.builder()
                                        .address(address.getAddress())
                                        .locationName(address.getLocation() != null ? address.getLocation().getName() : null)
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .build())
                .collect(Collectors.toList());
    }


    @Override
    public List<CompanyResponse> findAll() {
        return companyRepository.findAll()
                .stream().map(this::toResponse)
                .collect(Collectors.toList());
    }
    @Override
    public void updateAccountProfile(FormUpdateAccountCompany form) {
        AccountCompany accountCompany = jwtProvider.getCurrentAccountCompany();
        if (accountCompany == null) {
            throw new HttpBadRequest("Unauthorized: Company not found in token");
        }

       accountCompany.setFullName(form.getFullName());
        if (form.getPhone() != null) accountCompany.setPhone(form.getPhone());
        if (form.getDob() != null) accountCompany.setDob(form.getDob());
        if (form.getGender() != null) accountCompany.setGender(form.getGender());

        accountCompanyRepository.save(accountCompany);
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
                .link_Github(company.getLink_Github())
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
                .phone(accountCompany.getPhone())
                .dob(accountCompany.getDob())
                .gender(accountCompany.getGender())
                .status(accountCompany.isStatus())
                .isPremium(accountCompany.isPremium())
                .premiumUntil(accountCompany.getPremiumUntil())
                .company(CompanyResponse.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .email(company.getEmail())
                        .phone(company.getPhone())
                        .logo(company.getLogo())
                        .website(company.getWebsite())
                        .link_fb(company.getLink_fb())
                        .link_linkedin(company.getLink_linkedin())
                        .link_Github(company.getLink_Github())
                        .follower(company.getFollower())
                        .size(company.getSize())
                        .addresses(
                                company.getAddresses() != null
                                        ? company.getAddresses().stream()
                                        .map(address -> AddressCompanyResponse.builder()
                                                .id(address.getId())
                                                .address(address.getAddress())
                                                .build()
                                        ).toList()
                                        : null
                        )
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
                .link_Github(company.getLink_Github())
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
    @Override
    public CandidateResponse findCandidateById(Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found with id: " + id));

        return mapCandidateToResponse(candidate);
    }
    @Override
    public List<CandidateResponse> getSuitableCandidatesForCompanyJob(Long jobId) {

        AccountCompany company = jwtProvider.getCurrentAccountCompany();
        if (company == null) {
            throw new NoSuchElementException("Unauthorized: Company not found or token invalid");
        }


        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new NoSuchElementException("Job not found with id: " + jobId));

        if (!job.getCompany().getId().equals(company.getId())) {
            throw new SecurityException("You are not allowed to access this job");
        }

        Set<Skill> requiredSkills = job.getSkills();
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new NoSuchElementException("Job does not have any required skills");
        }

        Set<String> requiredSkillNames = requiredSkills.stream()
                .map(skill -> skill.getName().toLowerCase())
                .collect(Collectors.toSet());

        List<Candidate> candidates = candidateRepository.findAll()
                .stream()
                .filter(Candidate::isStatus)
                .toList();

        List<Candidate> suitable = candidates.stream()
                .filter(c -> c.getSkillCandidates() != null && !c.getSkillCandidates().isEmpty())
                .filter(c -> c.getSkillCandidates().stream()
                        .anyMatch(sc -> requiredSkillNames.contains(sc.getSkill().getName().toLowerCase())))
                .collect(Collectors.toList());

        Map<Candidate, Integer> candidateScores = new HashMap<>();

        for (Candidate candidate : suitable) {
            int score = 0;

            for (var sc : candidate.getSkillCandidates()) {
                String skillName = sc.getSkill().getName().toLowerCase();
                if (requiredSkillNames.contains(skillName)) {
                    score += levelRank(sc.getLevelJob());
                }
            }

            candidateScores.put(candidate, score);
        }


        List<Candidate> sorted = candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Candidate, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return sorted.stream()
                .map(this::mapCandidateToResponse)
                .collect(Collectors.toList());


    }

    private int levelRank(LevelJob levelJob) {
        if (levelJob == null || levelJob.getName() == null) return 0;
        return switch (levelJob.getName().toUpperCase()) {
            case "INTERN" -> 1;
            case "JUNIOR" -> 2;
            case "MIDDLE" -> 3;
            case "SENIOR" -> 4;
            default -> 0;
        };
    }
    @Override
    public List<CandidateResponse> getAllCandidatesBySkillScore() {
        List<Candidate> candidates = candidateRepository.findAll()
                .stream()
                .filter(Candidate::isStatus)
                .collect(Collectors.toList());

        Map<Candidate, Integer> candidateScores = new HashMap<>();

        for (Candidate candidate : candidates) {
            int totalScore = 0;

            if (candidate.getSkillCandidates() != null) {
                for (var sc : candidate.getSkillCandidates()) {
                    totalScore += levelRank(sc.getLevelJob());
                }
            }

            candidateScores.put(candidate, totalScore);
        }

        List<Candidate> sorted = candidateScores.entrySet().stream()
                .sorted(Map.Entry.<Candidate, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return sorted.stream()
                .map(candidate -> {
                    CandidateResponse response = mapCandidateToResponse(candidate);
                    return response;
                })
                .collect(Collectors.toList());
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
                .isOpen(candidate.getIsOpen())
                .Title(candidate.getTitle())
                .logo(candidate.getLogo())
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

}
