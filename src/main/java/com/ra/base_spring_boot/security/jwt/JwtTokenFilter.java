package com.ra.base_spring_boot.security.jwt;

import com.ra.base_spring_boot.security.principle.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final MyUserDetailsService userDetailsService; 
    private final MyCompanyDetailsService companyDetailsService; 
    private final JwtProvider jwtProvider;
    private final MyAdminDetailsService adminDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        String method = request.getMethod();

        boolean isPublicJobGet = method.equalsIgnoreCase("GET") && path.startsWith("/api/job");

        boolean isPublicAuth = path.equals("/api/v1/auth/candidate/login")
                || path.equals("/api/v1/auth/candidate/register")
                || path.equals("/api/v1/auth/company/login")
                || path.equals("/api/v1/auth/company/register")
                || path.equals("/api/v1/auth/admin/login")
                || path.equals("/api/v1/skills");

        if (isPublicJobGet ||
                isPublicAuth ||
                path.startsWith("/swagger") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/actuator")) {

            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                String email = jwtProvider.extractEmail(token);
                String type = jwtProvider.extractClaim(token, claims -> claims.get("type", String.class));

                if (email != null && type != null) {
                    switch (type) {
                        case "candidate" -> {
                            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                            if (jwtProvider.validateCandidateToken(token, ((MyUserDetails) userDetails).getCandidate())) {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                        case "company" -> {
                            UserDetails companyDetails = companyDetailsService.loadUserByUsername(email);
                            if (jwtProvider.validateCompanyToken(token, ((MyCompanyDetails) companyDetails).getAccountCompany())) {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(companyDetails, null, companyDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                        case "admin" -> {
                            UserDetails adminDetails = adminDetailsService.loadUserByUsername(email);
                            if (jwtProvider.validateAdminToken(token, ((MyAdminDetails) adminDetails).getAdmin())) {
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                            }
                        }
                        default -> log.debug("JwtTokenFilter: unknown token type '{}'", type);
                    }
                }
            }
        } catch (Exception e) {
            log.error("JWT Authentication error: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }


    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}