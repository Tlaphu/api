package com.ra.base_spring_boot.security.principle;

import com.ra.base_spring_boot.model.Admin;
import com.ra.base_spring_boot.repository.IAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyAdminDetailsService implements UserDetailsService {

    private final IAdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + email));
        return new MyAdminDetails(admin);
    }
}
