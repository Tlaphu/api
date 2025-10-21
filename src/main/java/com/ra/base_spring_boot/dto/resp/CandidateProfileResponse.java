package com.ra.base_spring_boot.dto.resp;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateProfileResponse {

    @NotBlank(message = "Full name is required")
    private String name;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;

    private String address;

    private Date dob;

    private Integer gender;

    private String link;
}
