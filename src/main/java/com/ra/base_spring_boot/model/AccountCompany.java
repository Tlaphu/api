package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

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

    @OneToMany(mappedBy = "accountCompany")
    private List<Company> companies;
}