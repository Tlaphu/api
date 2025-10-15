package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.resp.CompanyResponse;
import com.ra.base_spring_boot.services.ICompanyAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth/company")
@RequiredArgsConstructor
public class CompanyController {

    private final ICompanyAuthService companyAuthService;

    @GetMapping("/top20")
    public ResponseEntity<ResponseWrapper<List<CompanyResponse>>> getTop20() {
        List<CompanyResponse> list = companyAuthService.findTop20ByFollower();
        return ResponseEntity.ok(
                ResponseWrapper.<List<CompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(list)
                        .build()
        );
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<List<CompanyResponse>>> getAll() {
        List<CompanyResponse> list = companyAuthService.findAll();
        return ResponseEntity.ok(
                ResponseWrapper.<List<CompanyResponse>>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(list)
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseWrapper<CompanyResponse>> getById(@PathVariable Long id) {
        CompanyResponse response = companyAuthService.findById(id);
        return ResponseEntity.ok(
                ResponseWrapper.<CompanyResponse>builder()
                        .status(HttpStatus.OK)
                        .code(HttpStatus.OK.value())
                        .data(response)
                        .build()
        );
    }
}
