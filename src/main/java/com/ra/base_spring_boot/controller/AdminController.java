package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.services.IAdminService;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final IAdminService adminService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody FormLogin formLogin) {
        return ResponseEntity.ok(adminService.login(formLogin));
    }
}
