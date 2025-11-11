package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.dto.resp.CompanyResponse;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

public interface ICandidateAuthService {

    /**
     * Register a new candidate account
     */
    void register(FormRegisterCandidate formRegisterCandidate);

    void updateDescription(FormUpdateDescription form);
    void deleteDescription();

    /**
     * Login candidate with email + password
     */
    JwtResponse login(FormLogin formLogin);

    /**
     * Logout candidate (invalidate JWT token or session)
     */
    void logout(String token);

    /**
     * Handle forgot password via email & role
     */
    String forgotPassword(FormForgotPassword form);

    /**
     * Change candidate password with oldPassword & newPassword
     */
    void changePassword(FormChangePassword form);

    /**
     * Update candidate profile
     */
    void updateProfile(FormUpdateProfile form);

    void activateAccount(String token);

    void resetPassword(FormResetPassword form);
    CandidateResponse getCurrentCandidateProfile();

    @Transactional
    void addFavoriteCompany(Long companyId);

    @Transactional
    void removeFavoriteCompany(Long companyId);

    Set<CompanyResponse> getFavoriteCompanies();
}
