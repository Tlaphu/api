package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormAddressCompany;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormUpdateCompany;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile;
import com.ra.base_spring_boot.dto.resp.*;
import com.ra.base_spring_boot.model.Skill;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.ICompanyRepository;
import com.ra.base_spring_boot.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;
    private final ISkillService skillService;
    private final INotificationService notificationService;
    private final IBlacklistedWordService blacklistedWordService;
    private final ICompanyAuthService companyAuthService;

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


    @PutMapping("/companies/accounts/status/{id}") // Đổi tên endpoint
    public ResponseEntity<String> toggleCompanyAccountStatus(@PathVariable Long id) {
        // Gọi service và nhận lại trạng thái mới
        boolean isNowActive = adminService.activateCompanyAccount(id);

        String message;
        if (isNowActive) {
            message = "Company account successfully activated. Login credentials may have been sent if this was the first activation.";
        } else {
            message = "Company account successfully deactivated.";
        }

        return ResponseEntity.ok(message);
    }
    @GetMapping("/skill")
    public List<Skill> getAll() {
        return skillService.findAll();
    }

    @GetMapping("/skill/{id}")
    public Skill getById(@PathVariable Long id) {
        return skillService.findById(id);
    }

    @PostMapping("/skill")
    public Skill create(@RequestBody Skill skill) {
        return skillService.create(skill);
    }

    @PutMapping("/skill/{id}")
    public Skill update(@PathVariable Long id, @RequestBody Skill skill) {
        return skillService.update(id, skill);
    }

    @DeleteMapping("/skill/{id}")
    public String delete(@PathVariable Long id) {
        skillService.delete(id);
        return "Deleted skill with id: " + id;
    }
    @GetMapping("/admin/notifications")
    public ResponseEntity<?> getAllAdminNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotificationsForAdmin());
    }
    @GetMapping("/words")
    public List<String> listWords() {
        return blacklistedWordService.findAllWords();
    }

    @PostMapping("/words")
    public ResponseEntity<?> addWord(@RequestBody Map<String,String> body) {
        String w = body.get("word");
        blacklistedWordService.addWord(w);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/words/{id}")
    public ResponseEntity<?> remove(@PathVariable Long id) {
        blacklistedWordService.removeById(id);
        return ResponseEntity.ok().build();
    }
    /**
     * Admin cập nhật địa chỉ
     */
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressCompanyResponse> update(
            @PathVariable Long addressId,
            @RequestBody FormAddressCompany form
    ) {
        return ResponseEntity.ok(adminService.update(addressId, form));
    }

    /**
     * Admin xóa địa chỉ
     */
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<?> deleteAddress(@PathVariable Long addressId) {
        adminService.delete(addressId);
        return ResponseEntity.ok("Deleted successfully");
    }
}
