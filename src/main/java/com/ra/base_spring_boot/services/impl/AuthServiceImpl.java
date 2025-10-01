package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile;
import com.ra.base_spring_boot.dto.req.FormChangePassword;
import com.ra.base_spring_boot.dto.req.FormForgotPassword;
import com.ra.base_spring_boot.dto.resp.JwtResponse;
import com.ra.base_spring_boot.exception.HttpBadRequest;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.User;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.IUserRepository;
import com.ra.base_spring_boot.security.jwt.JwtProvider;
import com.ra.base_spring_boot.security.principle.MyUserDetails;
import com.ra.base_spring_boot.services.IAuthService;
import com.ra.base_spring_boot.services.IRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {
    private final IRoleService roleService;
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    @Override
    public void register(FormRegister formRegister) {
        Set<Role> roles = new HashSet<>();
        roles.add(roleService.findByRoleName(RoleName.ROLE_USER));
        User user = User.builder()
                .fullName(formRegister.getFullName())
                .username(formRegister.getUsername())
                .password(passwordEncoder.encode(formRegister.getPassword()))
                .status(true)
                .roles(roles)
                .build();
        userRepository.save(user);
    }

    @Override
    public JwtResponse login(FormLogin formLogin) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(formLogin.getUsername(), formLogin.getPassword()));
        } catch (AuthenticationException e) {
            throw new HttpBadRequest("Username or password is incorrect");
        }

        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();
        if (!userDetails.getUser().getStatus()) {
            throw new HttpBadRequest("your account is blocked");
        }

        return JwtResponse.builder()
                .accessToken(jwtProvider.generateToken(userDetails.getUsername()))
                .user(userDetails.getUser())
                .roles(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Override
    public void logout(String token) {
        
        System.out.println("Logout token: " + token);
    }

    @Override
    public String forgotPassword(FormForgotPassword form) {
        User user = userRepository.findByEmailAndRoles_Name(form.getEmail(), form.getRole())
                .orElseThrow(() -> new HttpBadRequest("User not found with email and role"));

        String resetToken = java.util.UUID.randomUUID().toString();

        System.out.println("Send reset password token to email: " + user.getEmail());

        return resetToken;
    }

    @Override
    public void changePassword(FormChangePassword form) {
        String username = jwtProvider.getUsernameFromCurrentToken();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        if (!passwordEncoder.matches(form.getOldPassword(), user.getPassword())) {
            throw new HttpBadRequest("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(form.getNewPassword()));
        userRepository.save(user);
    }
     @Override
    public void updateProfile(FormUpdateProfile form) {
        String username = jwtProvider.getUsernameFromCurrentToken();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new HttpBadRequest("User not found"));

        user.setFullName(form.getFullName());
        user.setEmail(form.getEmail());

        userRepository.save(user);
    }
}
