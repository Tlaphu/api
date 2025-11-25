package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.AccountCompanyResponse;
import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.dto.resp.CompanyResponse;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.model.AccountCompany;

import com.ra.base_spring_boot.model.Company;
import java.util.List;
public interface ICompanyAuthService {

    /**
     * Register a new company account
     */
    void register(FormRegisterCompany formRegisterCompany);

    AccountCompany getCurrentAccountCompany();

    /**
     * Login company with email + password
     */
    JwtResponse login(FormLogin formLogin);

    /**
     * Logout company (invalidate JWT token or session)
     */
    void logout(String token);

    AccountCompanyResponse getCurrentCompanyInfo();

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
    List<CompanyResponse> findTop20ByFollower();

    /**
     * Get all companies
     */
    List<CompanyResponse> findAll();

    void updateAccountProfile(FormUpdateAccountCompany form);

    /**
     * Get company by id
     */
    CompanyResponse findById(Long id);
    void activateAccount(String token);
    void resetPassword(FormResetPassword form);

    CandidateResponse findCandidateById(Long id);

    List<CandidateResponse> getSuitableCandidatesForCompanyJob(Long jobId);

    List<CandidateResponse> getAllCandidatesBySkillScore();

}

