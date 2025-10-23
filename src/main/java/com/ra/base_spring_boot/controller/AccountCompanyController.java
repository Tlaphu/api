package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.services.ICompanyAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/company")
@RequiredArgsConstructor
public class AccountCompanyController {

    private final ICompanyAuthService companyAuthService;

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<String>> register(@RequestBody FormRegisterCompany form) {
        companyAuthService.register(form);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.CREATED)
                        .code(HttpStatus.CREATED.value())
                        .data("Register successfully")
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<JwtResponse>> login(@RequestBody FormLogin formLogin) {
        JwtResponse response = companyAuthService.login(formLogin);
        return ResponseEntity.ok(
                ResponseWrapper.<JwtResponse>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(response)
                        .build()
        );
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ResponseWrapper<String>> forgotPassword(@RequestBody FormForgotPassword form) {

        companyAuthService.forgotPassword(form);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Password reset link has been sent to your email.")
                        .build()
        );
    }

    @PutMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<String>> handleResetPassword(@Valid @RequestBody FormResetPassword form) {
        companyAuthService.resetPassword(form);

        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Password has been reset successfully! You can now log in.")
                        .build()
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<ResponseWrapper<String>> changePassword(@RequestBody FormChangePassword form) {
        companyAuthService.changePassword(form);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Password updated successfully")
                        .build()
        );
    }

    @PutMapping("/update-profile")
    public ResponseEntity<ResponseWrapper<String>> updateProfile(@RequestBody FormUpdateCompany form) {
        companyAuthService.updateProfile(form);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Profile updated successfully")
                        .build()
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<ResponseWrapper<String>> logout(@RequestHeader("Authorization") String token) {
        companyAuthService.logout(token);
        return ResponseEntity.ok(
                ResponseWrapper.<String>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data("Logout successful")
                        .build()
        );
    }

    @GetMapping("/verify")
    public ResponseEntity<?> handleCompanyVerification(@RequestParam("token") String token) {
        companyAuthService.activateAccount(token);
        return ResponseEntity.ok(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Company account activated successfully! You can now log in.")
                        .build()
        );
    }
}
