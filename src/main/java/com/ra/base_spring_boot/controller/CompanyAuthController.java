package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.model.Company;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth/company")
@RequiredArgsConstructor
public class CompanyAuthController {

    private final ICompanyAuthService companyAuthService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody FormLogin formLogin) {
        JwtResponse jwtResponse = companyAuthService.login(formLogin);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(jwtResponse)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody FormRegisterCompany formRegisterCompany) {
        companyAuthService.register(formRegisterCompany);
        return ResponseEntity.created(URI.create("/api/v1/auth/company/register"))
                .body(ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data("Company registered successfully")
                        .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        companyAuthService.logout(token);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Logout successfully")
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody FormForgotPassword form) {
        String resetToken = companyAuthService.forgotPassword(form);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Reset password link sent. Token: " + resetToken)
                .build());
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody FormChangePassword form) {
        companyAuthService.changePassword(form);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Password changed successfully")
                .build());
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody FormUpdateCompany form) {
        companyAuthService.updateProfile(form);
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data("Company profile updated successfully")
                .build());
    }
    @GetMapping("/top-followers")
    public ResponseEntity<?> getTop20Companies() {
        List<Company> topCompanies = companyAuthService.findTop20ByFollower();
        
        return ResponseEntity.ok(ResponseWrapper.builder()
                .status(HttpStatus.OK)
                .code(200)
                .data(topCompanies)
                .build());
    }
}

