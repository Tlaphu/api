package com.ra.base_spring_boot.security.principle;


import com.ra.base_spring_boot.model.AccountCompany;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyCompanyDetailsService implements UserDetailsService {

    private final IAccountCompanyRepository accountCompanyRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AccountCompany accountCompany = accountCompanyRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " not found"));

        return MyCompanyDetails.builder()
                .accountCompany(accountCompany)
                .authorities(accountCompany.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRoleName().toString()))
                        .toList())
                .build();
    }
}

