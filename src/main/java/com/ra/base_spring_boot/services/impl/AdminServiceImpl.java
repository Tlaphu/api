package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Admin;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyAdminDetails;
import com.ra.base_spring_boot.services.IAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements IAdminService {

    private final AuthenticationManager adminAuthManager; // Bean từ SecurityConfig
    private final JwtProvider jwtProvider;

    @Override
    public JwtResponse login(FormLogin formLogin) {
        try {
            Authentication authentication = adminAuthManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getEmail(), formLogin.getPassword())
            );

            MyAdminDetails adminDetails = (MyAdminDetails) authentication.getPrincipal();
            Admin admin = adminDetails.getAdmin();

            Set<String> roles = adminDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
            String token = jwtProvider.generateAdminToken(admin, roles);

            return JwtResponse.builder()
                    .accessToken(token)
                    .admin(admin)
                    .roles(roles)
                    .build();

        } catch (Exception e) {
            throw new HttpBadRequest("Wrong email");
        }
    }
}
