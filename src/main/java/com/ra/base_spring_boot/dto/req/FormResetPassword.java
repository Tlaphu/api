package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormResetPassword {
    
    @NotBlank(message = "Token is required")
    private String token; 
    
    @NotBlank(message = "New password is required")
    private String newPassword;
    
    @NotBlank(message = "Confirm new password is required")
    private String confirmNewPassword;

    
}