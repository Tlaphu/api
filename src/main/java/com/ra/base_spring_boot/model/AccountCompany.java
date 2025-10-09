package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountCompany {
    @Id
    private String id;

    private String email;
    private String password;

    @OneToOne(mappedBy = "accountCompany", cascade = CascadeType.ALL)
    @JsonBackReference
    private Company company;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_company_roles",
            joinColumns = @JoinColumn(name = "account_company_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}