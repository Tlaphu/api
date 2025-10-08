package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FormRegisterCompany {

    @NotBlank(message = "Full name is required")
    private String name;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm Password is required")
    private String confirmPassword;

    @NotBlank(message = "Phone is required")
    private String phone;
    private String logo;
    private String website;
    private String linkFb;
    private String linkLinkedin;
    private String description;
    private Integer size;
    private Integer follower;
    private String typeCompanyId;

    private FormAddressCompany address;
}


