package com.ra.base_spring_boot.dto.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormUpdateProfile {

    @NotBlank(message = "Full name is required")
    private String name;

    @Email(message = "Email is invalid")
    @NotBlank(message = "Email is required")
    private String email;

    private String phone;

    private String address;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date dob;

    private Integer gender; // 0 = female, 1 = male, 2 = other

    private String linkFb;
    private String linkLinkedin;
    private String linkGit;

    // Nếu muốn cho phép update trạng thái tìm việc
    private Integer isOpen; // 0 = đóng, 1 = mở
}
