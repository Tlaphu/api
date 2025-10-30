package com.ra.base_spring_boot.config;


import com.ra.base_spring_boot.model.Admin;
import com.ra.base_spring_boot.model.Role;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.IAdminRepository;
import com.ra.base_spring_boot.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final IAdminRepository adminRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedDefaultAdmin();
    }


    private void seedDefaultAdmin() {
        if (adminRepository.count() == 0) {
            Role adminRole = roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("ROLE_ADMIN not found"));

            Admin admin = Admin.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("123456"))
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();

            adminRepository.save(admin);
            System.out.println("Default admin created: admin@gmail.com / 123456");
        }
    }
}

