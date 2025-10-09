package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

public interface ICandidateAuthService {

    /**
     * Register a new candidate account
     */
    void register(FormRegisterCandidate formRegisterCandidate);

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

}
