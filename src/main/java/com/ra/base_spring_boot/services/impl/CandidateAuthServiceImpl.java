package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.ICandidateAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.EmailService; 
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

    private final IRoleService roleService;
    private final ICandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager candidateAuthManager;
    private final JwtProvider jwtProvider;
    private final EmailService emailService; 
    
    // URL CỨNG CHO SERVER (Cần thay thế bằng giá trị từ file properties trong thực tế)
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
        
        // 1. TẠO MÃ KÍCH HOẠT
        String verificationToken = UUID.randomUUID().toString();
        
        Candidate candidate = Candidate.builder()
                .name(formRegisterCandidate.getName())
                .email(formRegisterCandidate.getEmail())
                .password(passwordEncoder.encode(formRegisterCandidate.getPassword()))
                .phone(formRegisterCandidate.getPhone())
                .address(formRegisterCandidate.getAddress())
                .dob(formRegisterCandidate.getDob())
                .gender(formRegisterCandidate.getGender())
                .link(formRegisterCandidate.getLink())
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
   
    String resetToken = UUID.randomUUID().toString();
   
    candidate.setResetToken(resetToken); 
    candidateRepository.save(candidate); 
   
    

    String resetLink = BASE_URL.replace("/api/v1/auth/candidate", "") // Lấy domain + port
                        + "/reset-password?token=" + resetToken; 
    
    
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
        candidate.setAddress(form.getAddress());
        candidate.setDob(form.getDob());
        candidate.setGender(form.getGender());
        candidate.setLink(form.getLink());
        candidate.setUpdated_at(new Date());

        candidateRepository.save(candidate);
    }
    
    @Override
    public void activateAccount(String token) {
        Candidate candidate = candidateRepository.findByVerificationToken(token)
                .orElseThrow(() -> new HttpBadRequest("Invalid or expired verification token"));
        
        candidate.setStatus(true); 
        candidate.setVerificationToken(null); 
        candidateRepository.save(candidate);
    }
}