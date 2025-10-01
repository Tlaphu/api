package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.*;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

public interface IAuthService
{

    void register(FormRegister formRegister);

    JwtResponse login(FormLogin formLogin);

    void logout(String token);

    String forgotPassword(FormForgotPassword form);

    void changePassword(FormChangePassword form);

    void updateProfile(FormUpdateProfile form);

}
