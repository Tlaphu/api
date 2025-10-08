package com.ra.base_spring_boot.security.principle;

import com.ra.base_spring_boot.model.AccountCompany;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "accountCompany")
public class MyCompanyDetails implements UserDetails {

    private AccountCompany accountCompany;
    private Collection<? extends GrantedAuthority> authorities;

    public AccountCompany getAccountCompany() {
        return this.accountCompany;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.accountCompany.getPassword();
    }

    @Override
    public String getUsername() {
        return this.accountCompany.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {

        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {

        return true;
    }
}

