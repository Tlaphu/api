package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.CandidateResponse;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ICandidateAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth/candidate")
@RequiredArgsConstructor
public class CandidateAuthController {

    private final ICandidateAuthService authService;
    private final JwtProvider jwtProvider;

    /**
     * @param formLogin FormLogin
     * @apiNote handle login with { email , password }
     */
    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLogin formLogin) {
        JwtResponse jwtResponse = authService.login(formLogin);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(jwtResponse)
                        .build()
        );
    }

    /**
     * @param formRegisterCandidate FormRegister
     * @apiNote handle register candidate with profile info
     */
    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegisterCandidate formRegisterCandidate) {
        authService.register(formRegisterCandidate);
        return ResponseEntity.created(URI.create("api/v1/auth/candidate/register"))
                .body(
                        ResponseWrapper.builder()
                                .status(HttpStatus.CREATED)
                                .code(201)
                                .data("Candidate registered successfully")
                                .build()
                );
    }

    /**
     * @apiNote handle logout (invalidate token or session)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> handleLogout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Logout successfully")
                        .build()
        );
    }

    /**
     * @param formForgotPassword FormForgotPassword
     * @apiNote handle forgot password via email & role
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> handleForgotPassword(@Valid @RequestBody FormForgotPassword formForgotPassword) {

        authService.forgotPassword(formForgotPassword);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password reset link has been sent to your email.")
                        .build()
        );
    }

    /**
     * @param @apiNote handle reset password with resetToken & newPassword
     */
    @PutMapping("/reset-password")
    public ResponseEntity<?> handleResetPassword(@Valid @RequestBody FormResetPassword form) {
        authService.resetPassword(form);

        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Password has been reset successfully! You can now log in.")
                        .build()
        );
    }

    /**
     * @param formChangePassword FormChangePassword
     * @apiNote handle change password with oldPassword & newPassword
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> handleChangePassword(@Valid @RequestBody FormChangePassword formChangePassword) {
        authService.changePassword(formChangePassword);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password changed successfully")
                        .build()
        );
    }

    /**
     * @param formUpdateProfile FormUpdateProfile
     * @apiNote handle update candidate profile
     */
    @PutMapping("/update-profile")
    public ResponseEntity<?> handleUpdateProfile(@Valid @RequestBody FormUpdateProfile formUpdateProfile) {
        authService.updateProfile(formUpdateProfile);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Candidate profile updated successfully")
                        .build()
        );
    }

    @GetMapping("/verify")
    public ResponseEntity<?> handleCandidateVerification(@RequestParam("token") String token) {
        authService.activateAccount(token);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Candidate account activated successfully! You can log in now.") 
                        .build()
        );
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentCandidateProfile() {
        CandidateResponse candidateResponse = authService.getCurrentCandidateProfile();
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(candidateResponse)
                        .build()
        );
    }

}
