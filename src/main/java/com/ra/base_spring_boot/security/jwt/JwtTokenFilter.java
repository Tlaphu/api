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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

        // Đã thêm isPublicCV vào điều kiện logic
        boolean isPublicCV = path.startsWith("/api/v1/candidate/cv/public/");

        boolean isPublicJobGet = method.equalsIgnoreCase("GET") && (
                path.equals("/api/job") ||
                        path.matches("^/api/job/\\d+$") ||
                        path.equals("/api/job/featured") ||
                        path.equals("/api/job/stats")||
                        path.equals("/api/job/by-skills")
        );
        boolean isPublicAuth = path.equals("/api/v1/auth/candidate/login")
                || path.equals("/api/v1/auth/candidate/register")
                || path.equals("/api/v1/auth/company/login")
                || path.equals("/api/v1/auth/company/register")
                || path.equals("/api/v1/auth/admin/login")
                || path.equals("/api/v1/skills");

        // ĐÃ SỬA: Thêm isPublicCV vào điều kiện IF
        if (isPublicCV || isPublicJobGet || isPublicAuth || path.startsWith("/swagger")
                || path.startsWith("/v3/api-docs") || path.startsWith("/actuator")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromRequest(request);
            if (token != null) {
                Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                if (currentAuth == null || currentAuth.getPrincipal() instanceof String) {

                    String email = jwtProvider.extractEmail(token);
                    String type = jwtProvider.extractClaim(token, claims -> claims.get("type", String.class));

                    log.info("JWT Token detected. Type: {}, Email: {}", type, email);

                    if (email != null && type != null) {
                        switch (type) {
                            case "candidate" -> {
                                setCandidateAuthentication(email, token, request);

                            }
                            case "company" -> {
                                setCompanyAuthentication(email, token, request);

                            }
                            case "admin" -> {
                                setAdminAuthentication(email, token, request);

                            }
                            default -> log.warn("JwtTokenFilter: unknown token type '{}'", type);
                        }

                    } else {
                        log.warn("JWT Token missing email or type");
                    }
                } else {
                    log.info("Authentication already exists: {}", currentAuth.getPrincipal().getClass().getSimpleName());
                }
            } else {
                log.warn("No JWT token found in request header");
            }
        } catch (Exception e) {
            log.error("JWT Authentication error: {}", e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void setCandidateAuthentication(String email, String token, HttpServletRequest request) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        log.info("Validating candidate token for email: {}", email);
        if (jwtProvider.validateCandidateToken(token, ((MyUserDetails) userDetails).getCandidate())) {
            log.info("Candidate token valid. Setting authentication.");
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return;
        } else {
            log.warn("Candidate token invalid for email: {}", email);
        }
    }

    private void setCompanyAuthentication(String email, String token, HttpServletRequest request) {
        UserDetails companyDetails = companyDetailsService.loadUserByUsername(email);
        log.info("Validating company token for email: {}", email);
        if (jwtProvider.validateCompanyToken(token, ((MyCompanyDetails) companyDetails).getAccountCompany())) {
            log.info("Company token valid. Setting authentication.");
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(companyDetails, null, companyDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return;
        } else {
            log.warn("Company token invalid for email: {}", email);
        }
    }

    private void setAdminAuthentication(String email, String token, HttpServletRequest request) {
        UserDetails adminDetails = adminDetailsService.loadUserByUsername(email);
        log.info("Validating admin token for email: {}", email);
        if (jwtProvider.validateAdminToken(token, ((MyAdminDetails) adminDetails).getAdmin())) {
            log.info("Admin token valid. Setting authentication.");
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(adminDetails, null, adminDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return;
        } else {
            log.warn("Admin token invalid for email: {}", email);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}