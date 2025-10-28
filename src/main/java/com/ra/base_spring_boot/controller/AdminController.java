package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.services.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody FormLogin formLogin) {
        return ResponseEntity.ok(adminService.login(formLogin));
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyResponse>> getAllCompanies() {
        return ResponseEntity.ok(adminService.findAll());
    }

    @PutMapping("/companies/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable Long id,
            @RequestBody FormUpdateCompany form
    ) {
        return ResponseEntity.ok(adminService.updateCompany(id, form));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable Long id) {
        adminService.deleteCompany(id);
        return ResponseEntity.ok("Company deleted successfully");
    }

    @GetMapping("/candidates")
    public ResponseEntity<List<CandidateResponse>> getAllCandidates() {
        return ResponseEntity.ok(adminService.findAllCandidates());
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<CandidateResponse> updateCandidate(
            @PathVariable Long id,
            @RequestBody FormUpdateProfile form
    ) {
        return ResponseEntity.ok(adminService.updateCandidate(id, form));
    }

    @DeleteMapping("/candidates/{id}")
    public ResponseEntity<?> deleteCandidate(@PathVariable Long id) {
        adminService.deleteCandidate(id);
        return ResponseEntity.ok("Candidate deleted successfully");
    }

    @GetMapping("/companies/accounts")
    public ResponseEntity<List<AccountCompanyResponse>> getAllAccountsCompany() {
        return ResponseEntity.ok(adminService.findAllAccountsCompany());
    }

    @DeleteMapping("/companies/accounts/{id}")
    public ResponseEntity<?> deleteAccountCompany(@PathVariable Long id) {
        adminService.deleteAccountCompany(id);
        return ResponseEntity.ok("Account company deleted successfully");
    }
}
