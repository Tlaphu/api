package com.ra.base_spring_boot.model;

import jakarta.persistence.Entity;
import lombok.*;


import jakarta.persistence.*;

@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;
    private String email;

}

