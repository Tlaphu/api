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
    private String id;
    public void setId(String id) {
        this.id = id;
    }
    private String name;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

    @OneToMany(mappedBy = "typeCompany")
    private List<Company> companies;
}