package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class TypeCompany {
    @Id
<<<<<<< HEAD
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
>>>>>>> 05fa726ec0f77df812a93ae1d3fdd29aebdb058d
    private Long id;
    private String name;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

    @OneToMany(mappedBy = "typeCompany")
    private List<Company> companies;
}