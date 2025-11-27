package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.AccountCompany;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.services.ICandidateCVService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/company/cv")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_COMPANY') or hasAuthority('ROLE_ADMIN')")
public class CompanyCandidateController {

    private final ICandidateCVService candidateCVService;
    private final JwtProvider jwtProvider;

    /**
     * Lấy companyId từ token (chỉ khi user là COMPANY)
     */
    private Long getCurrentCompanyId() {
        AccountCompany company = jwtProvider.getCurrentAccountCompany();
        if (company == null) {
            throw new HttpBadRequest("Cần đăng nhập bằng tài khoản Công ty.");
        }
        return company.getId();
    }

    /**
     * API tải CV – admin tải tất cả, company tải CV ứng viên apply vào job của công ty
     */
    @GetMapping("/{cvId}/download")
    public ResponseEntity<byte[]> downloadCandidateCv(@PathVariable Long cvId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        Long companyId = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))
                ? null                // ADMIN → bỏ check quyền
                : getCurrentCompanyId(); // COMPANY → bắt buộc check quyền

        
        byte[] fileBytes = candidateCVService.downloadCvForCompany(cvId, companyId);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "CV_" + cvId + ".pdf");
        headers.setContentLength(fileBytes.length);

        return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
    }
}
