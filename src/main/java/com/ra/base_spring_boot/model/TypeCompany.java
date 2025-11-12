package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private String name;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
    @JsonIgnore
    @OneToMany(mappedBy = "typeCompany")
    private List<Company> companies;
}