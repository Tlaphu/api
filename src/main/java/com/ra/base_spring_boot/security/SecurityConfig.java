package com.ra.base_spring_boot.security;

import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.security.exception.AccessDenied;
import com.ra.base_spring_boot.security.exception.JwtEntryPoint;
import com.ra.base_spring_boot.security.jwt.JwtTokenFilter;
import com.ra.base_spring_boot.security.principle.MyAdminDetailsService;
import com.ra.base_spring_boot.security.principle.MyCompanyDetailsService;
import com.ra.base_spring_boot.security.principle.MyUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtEntryPoint jwtEntryPoint;
    private final AccessDenied accessDenied;
    private final JwtTokenFilter jwtTokenFilter;

    private final MyUserDetailsService candidateDetailsService;
    private final MyCompanyDetailsService companyDetailsService;
    private final MyAdminDetailsService adminDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider candidateAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(candidateDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider companyAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(companyDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider adminAuthProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(adminDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Primary
    @Bean(name = "candidateAuthManager")
    public AuthenticationManager candidateAuthManager() {
        return new ProviderManager(List.of(candidateAuthProvider()));
    }

    @Bean(name = "companyAuthManager")
    public AuthenticationManager companyAuthManager() {
        return new ProviderManager(List.of(companyAuthProvider()));
    }

    @Bean(name = "adminAuthManager")
    public AuthenticationManager adminAuthManager() {
        return new ProviderManager(List.of(adminAuthProvider()));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173"
        ));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // ĐÃ THÊM PATCH
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        String candidateAuthPattern = "/api/v1/auth/candidate/**";
        String companyAuthPattern = "/api/v1/auth/company/**";

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/job/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/company/top20").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/auth/company/{id}").permitAll()
                        .requestMatchers("/api/v1/public/cv/**").permitAll()
                        .requestMatchers("/api/v1/auth/company/login").permitAll()
                        .requestMatchers("/api/v1/auth/company/register").permitAll()
                        .requestMatchers("/api/v1/auth/company/forgot-password").permitAll()
                        .requestMatchers("/api/v1/auth/company/reset-password").permitAll()
                        .requestMatchers("/api/v1/auth/company/verify").permitAll()
                        .requestMatchers("/api/v1/auth/candidate/login").permitAll()
                        .requestMatchers("/api/v1/skills").permitAll()
                        .requestMatchers("/api/v1/auth/candidate/register").permitAll()
                        .requestMatchers("/api/v1/auth/candidate/forgot-password").permitAll()
                        .requestMatchers("/api/v1/auth/candidate/reset-password").permitAll()
                        .requestMatchers("/api/v1/auth/candidate/verify").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/location/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/reviews").permitAll()
                        .requestMatchers("/api/payment/create", "/api/payment/vnpay_return").permitAll()

                        .requestMatchers("/api/payment/**").permitAll()
                        .requestMatchers(
                                "/api/v1/auth/candidate/login",
                                "/api/v1/auth/candidate/register",
                                "/api/v1/auth/candidate/forgot-password",
                                "/api/v1/auth/candidate/reset-password",
                                "/api/v1/auth/candidate/verify"
                        ).permitAll()
                        .requestMatchers("/api/v1/auth/candidate/**").hasAuthority("ROLE_CANDIDATE")

                        .requestMatchers(companyAuthPattern).permitAll()

                        .requestMatchers("/api/v1/admin/login").permitAll()
                        .requestMatchers("/api/v1/auth/company/change-password",
                                "/api/v1/auth/company/update-profile",
                                "/api/v1/auth/company/logout")
                        .hasAuthority(RoleName.ROLE_COMPANY.toString())

                        .requestMatchers("/api/v1/admin/**").hasAuthority(RoleName.ROLE_ADMIN.toString())
                        .requestMatchers("/api/v1/candidate/**").hasAuthority(RoleName.ROLE_CANDIDATE.toString())
                        .requestMatchers("/api/v1/company/**")
                        .hasAnyAuthority(RoleName.ROLE_COMPANY.toString(), RoleName.ROLE_ADMIN.toString())

                        .requestMatchers("/api/v1/job-candidates/**")
                        .hasAnyAuthority(RoleName.ROLE_ADMIN.toString(), RoleName.ROLE_COMPANY.toString(), RoleName.ROLE_CANDIDATE.toString())
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(jwtEntryPoint)
                        .accessDeniedHandler(accessDenied)
                )
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
