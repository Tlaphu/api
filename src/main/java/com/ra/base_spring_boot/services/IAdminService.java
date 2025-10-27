package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.req.FormLogin;
import com.ra.base_spring_boot.dto.resp.JwtResponse;

public interface IAdminService {
    JwtResponse login(FormLogin formLogin);
}
