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

>>>>>>> d194026180307206889f1bd83462d97eb980b4d7
    private Long id;
    private String name;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

    @OneToMany(mappedBy = "typeCompany")
    private List<Company> companies;
}