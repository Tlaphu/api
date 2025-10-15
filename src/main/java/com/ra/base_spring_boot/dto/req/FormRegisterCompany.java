package com.ra.base_spring_boot.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormRegisterCompany {


    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Email(message = "Email tài khoản không hợp lệ")
    @NotBlank(message = "Email tài khoản không được để trống")
    private String email;
    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được để trống")
    private String confirmPassword;


    @NotBlank(message = "Tên công ty không được để trống")
    private String companyName;

    @NotBlank(message = "Địa điểm làm việc không được để trống")
    private String address;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @Email(message = "Email công ty không hợp lệ")
    @NotBlank(message = "Email công ty không được để trống")
    private String companyEmail;
}



