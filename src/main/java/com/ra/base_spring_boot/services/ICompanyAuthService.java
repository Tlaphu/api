package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

public interface ICompanyAuthService {

    /**
     * Register a new company account
     */
    void register(FormRegisterCompany formRegisterCompany);

    /**
     * Login company with email + password
     */
    JwtResponse login(FormLogin formLogin);

    /**
     * Logout company (invalidate JWT token or session)
     */
    void logout(String token);

    /**
     * Handle forgot password via email & role
     */
    String forgotPassword(FormForgotPassword form);

    /**
     * Change company password with oldPassword & newPassword
     */
    void changePassword(FormChangePassword form);

    /**
     * Update company profile
     */
    void updateProfile(FormUpdateCompany form);

}

