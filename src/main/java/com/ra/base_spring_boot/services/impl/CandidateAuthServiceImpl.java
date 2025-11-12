package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.AccountCompany;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ICandidateAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.EmailService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CandidateAuthServiceImpl implements ICandidateAuthService {
    private final ICompanyRepository companyRepository;
    private final IRoleService roleService;
    private final ICandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager candidateAuthManager;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private static final String BASE_URL = "http://localhost:8080/api/v1/auth/candidate";

    @Override
    public void register(FormRegisterCandidate formRegisterCandidate) {
        if (candidateRepository.existsByEmail(formRegisterCandidate.getEmail())) {
            throw new HttpBadRequest("Email is already registered");
        }

        if (!formRegisterCandidate.getPassword().equals(formRegisterCandidate.getConfirmPassword())) {
            throw new HttpBadRequest("Passwords do not match");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_CANDIDATE));

        String verificationToken = UUID.randomUUID().toString();

        Candidate candidate = Candidate.builder()
                .name(formRegisterCandidate.getName())
                .email(formRegisterCandidate.getEmail())
                .password(passwordEncoder.encode(formRegisterCandidate.getPassword()))
                .phone(formRegisterCandidate.getPhone())
                .roles(roles)
                .created_at(new Date())
                .updated_at(new Date())
                .isOpen(1)
                .verificationToken(verificationToken)
                .status(false)
                .build();

        candidateRepository.save(candidate);

        String confirmationLink = BASE_URL + "/verify?token=" + verificationToken;

        emailService.sendVerificationEmail(
                formRegisterCandidate.getEmail(),
                formRegisterCandidate.getName(),
                confirmationLink
        );
        System.out.println(" Activation email sent to: " + formRegisterCandidate.getEmail());
    }

    @Override
    public void activateAccount(String token) {

        Candidate candidate = candidateRepository.findByVerificationToken(token)
                .orElseThrow(() -> new HttpBadRequest("Invalid or expired verification token"));

        candidate.setStatus(true);
        candidate.setVerificationToken(null);
        candidateRepository.save(candidate);

        emailService.sendRegistrationSuccessEmail(candidate.getEmail(), candidate.getName(), "Candidate");
    }

    @Override
    public JwtResponse login(FormLogin formLogin) {
        try {
            Authentication authentication = candidateAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );

            MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
            Candidate candidate = userDetails.getCandidate();

            if (!candidate.isStatus()) {

                throw new HttpBadRequest("Account is not activated. Please check your email for the activation link.");
            }

            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());

            String token = jwtProvider.generateCandidateToken(candidate, roles);

            return JwtResponse.builder()
                    .accessToken(token)
                    .candidate(candidate)
                    .roles(roles)
                    .build();

        } catch (AuthenticationException e) {
            throw new HttpBadRequest("Email or password wrong");
        }
    }

    @Override
    public void logout(String token) {
        System.out.println("Logout token: " + token);
    }

    @Override
    public String forgotPassword(FormForgotPassword form) {
        Candidate candidate = candidateRepository
                .findByEmail(form.getEmail())
                .orElseThrow(() -> new HttpBadRequest("Candidate account not found with this email."));
        if (!candidate.isStatus()) {
            throw new HttpBadRequest("Account is not activated.");
        }
        String resetToken = UUID.randomUUID().toString();

        candidate.setResetToken(resetToken);
        candidateRepository.save(candidate);

        String frontendBaseUrl = "http://localhost:5173";
        String resetLink = frontendBaseUrl + "/reset-password?token=" + resetToken;

        emailService.sendResetPasswordEmail(
                candidate.getEmail(),
                candidate.getName(),
                resetLink
        );

        return "Password reset link sent to email.";
    }

    @Override
    public void resetPassword(FormResetPassword form) {
        if (!form.getNewPassword().equals(form.getConfirmNewPassword())) {
            throw new HttpBadRequest("New passwords do not match.");
        }

        Candidate candidate = candidateRepository.findByResetToken(form.getToken())
                .orElseThrow(() -> new HttpBadRequest("Invalid or expired reset token."));

        candidate.setPassword(passwordEncoder.encode(form.getNewPassword()));
        candidate.setResetToken(null);

        candidateRepository.save(candidate);
    }

    @Override
    public void changePassword(FormChangePassword form) {
        Candidate candidate = jwtProvider.getCurrentCandidate();
        if (candidate == null) {
            throw new HttpBadRequest("Unauthorized: Candidate not found");
        }

        if (!passwordEncoder.matches(form.getOldPassword(), candidate.getPassword())) {
            throw new HttpBadRequest("Old password is incorrect");
        }

        candidate.setPassword(passwordEncoder.encode(form.getNewPassword()));
        candidate.setUpdated_at(new Date());

        candidateRepository.save(candidate);
    }

    @Override
    public void updateProfile(FormUpdateProfile form) {
        String email = jwtProvider.getCandidateUsername();
        Candidate candidate = candidateRepository.findByEmail(email)
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));

        candidate.setName(form.getName());
        candidate.setEmail(form.getEmail());
        candidate.setPhone(form.getPhone());
        candidate.setDescription(form.getDescription());
        candidate.setTitle(form.getTitle());
        candidate.setAddress(form.getAddress());
        candidate.setDob(form.getDob());
        candidate.setTitle(form.getTitle());
        candidate.setGender(form.getGender());
        candidate.setLink(form.getLink());
        candidate.setUpdated_at(new Date());

        candidateRepository.save(candidate);
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateResponse getCurrentCandidateProfile() {
        Candidate current = jwtProvider.getCurrentCandidate();
        if (current == null) {
            throw new HttpBadRequest("Unauthorized: Candidate not found or token invalid");
        }

        Candidate candidate = candidateRepository.findById(current.getId())
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));

        return mapCandidateToResponse(candidate);
    }

    @Override
    public void updateDescription(FormUpdateDescription form) {
        Candidate candidate = jwtProvider.getCurrentCandidate();
        if (candidate == null) {
            throw new HttpBadRequest("Unauthorized: Candidate not found");
        }

        candidate.setDescription(form.getDescription());
        candidate.setUpdated_at(new Date());
        candidateRepository.save(candidate);
    }

    @Override
    public void deleteDescription() {
        Candidate candidate = jwtProvider.getCurrentCandidate();
        if (candidate == null) {
            throw new HttpBadRequest("Unauthorized: Candidate not found");
        }

        candidate.setDescription(null);
        candidate.setUpdated_at(new Date());
        candidateRepository.save(candidate);
    }
    @Transactional
    @Override
    public void addFavoriteCompany(Long companyId) {
        Candidate current = jwtProvider.getCurrentCandidate();
        Candidate candidate = candidateRepository.findByIdWithFavoriteCompanies(current.getId())
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));

        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new HttpBadRequest("Company not found with id: " + companyId));

        if (candidate.getFavoriteCompanies().contains(company)) {
            throw new HttpBadRequest("Company is already in favorites.");
        }

        candidate.getFavoriteCompanies().add(company);
        candidateRepository.save(candidate);
    }

    @Transactional
    @Override
    public void removeFavoriteCompany(Long companyId) {
        Candidate current = jwtProvider.getCurrentCandidate();
        Candidate candidate = candidateRepository.findByIdWithFavoriteCompanies(current.getId())
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));


        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new HttpBadRequest("Company not found with id: " + companyId));

        if (!candidate.getFavoriteCompanies().contains(company)) {
            throw new HttpBadRequest("Company is not in favorites.");
        }

        candidate.getFavoriteCompanies().remove(company);
        candidateRepository.save(candidate);
    }

    @Override
    @Transactional
    public Set<CompanyResponse> getFavoriteCompanies() {
        Candidate current = jwtProvider.getCurrentCandidate();
        Candidate candidate = candidateRepository.findByIdWithFavoriteCompanies(current.getId())
                .orElseThrow(() -> new HttpBadRequest("Candidate not found"));


        return candidate.getFavoriteCompanies().stream()
                .map(company -> CompanyResponse.builder()
                        .id(company.getId())
                        .name(company.getName())
                        .email(company.getEmail())
                        .description(company.getDescription())
                        .build())
                .collect(Collectors.toSet());
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
