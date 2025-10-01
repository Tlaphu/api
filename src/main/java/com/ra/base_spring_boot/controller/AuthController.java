package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.dto.ResponseWrapper;
import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.req.FormRegister;
import com.ra.base_spring_boot.dto.req.FormUpdateProfile; 
import com.ra.base_spring_boot.dto.req.FormChangePassword;
import com.ra.base_spring_boot.dto.req.FormForgotPassword;
import com.ra.base_spring_boot.services.IAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController
{
    private final IAuthService authService;

    /**
     * @param formLogin FormLogin
     * @apiNote handle login with { username , password }
     */
    @PostMapping("/login")
    public ResponseEntity<?> handleLogin(@Valid @RequestBody FormLogin formLogin)
    {
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data(authService.login(formLogin))
                        .build()
        );
    }

    /**
     * @param formRegister FormRegister
     * @apiNote handle register with { fullName , username , password }
     */
    @PostMapping("/register")
    public ResponseEntity<?> handleRegister(@Valid @RequestBody FormRegister formRegister)
    {
        authService.register(formRegister);
        return ResponseEntity.created(URI.create("api/v1/auth/register")).body(
                ResponseWrapper.builder()
                        .status(HttpStatus.CREATED)
                        .code(201)
                        .data("Register successfully")
                        .build()
        );
    }

        /**
     * @apiNote handle logout (invalidate token or session)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> handleLogout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Logout successfully")
                        .build()
        );
    }

     /**
     * @param formForgotPassword FormForgotPassword
     * @apiNote handle forgot password via email & role
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> handleForgotPassword(@Valid @RequestBody FormForgotPassword formForgotPassword) {
        String resetToken = authService.forgotPassword(formForgotPassword);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Reset password link has been sent to email. Token: " + resetToken)
                        .build()
        );
    }
       /**
     * @param formChangePassword FormChangePassword
     * @apiNote handle change password with oldPassword & newPassword
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> handleChangePassword(@Valid @RequestBody FormChangePassword formChangePassword) {
        authService.changePassword(formChangePassword);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Password changed successfully")
                        .build()
        );
    }
        /**
     * @param formUpdateProfile FormUpdateProfile
     * @apiNote handle update user profile
     */
    @PutMapping("/update-profile")
    public ResponseEntity<?> handleUpdateProfile(@Valid @RequestBody FormUpdateProfile formUpdateProfile) {
        authService.updateProfile(formUpdateProfile);
        return ResponseEntity.ok().body(
                ResponseWrapper.builder()
                        .status(HttpStatus.OK)
                        .code(200)
                        .data("Profile updated successfully")
                        .build()
        );
    }

}
