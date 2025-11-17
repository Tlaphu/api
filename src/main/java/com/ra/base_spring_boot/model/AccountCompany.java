package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ra.base_spring_boot.model.base.BaseObject;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AccountCompany {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String password;
    private String phone;
    private Integer gender;
    @Builder.Default
    private boolean status = false;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private String verificationToken;
    @Column(unique = true) 
    private String resetToken;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
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
    @Builder.Default
    private boolean isPremium = false;


    @Temporal(TemporalType.TIMESTAMP)
    private Date premiumUntil;
}