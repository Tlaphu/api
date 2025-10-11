package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Company {
    @Id
    private String id;

    @OneToOne
    @JoinColumn(name = "account_company_id")
    @JsonManagedReference
    private AccountCompany accountCompany;

    @ManyToOne
    @JoinColumn(name = "typeCompany_id")
    private TypeCompany typeCompany;
    private String phone;
    private String name;
    private String logo;
    private String website;
    private String link_fb;
    private String link_linkedin;
    private Integer follower;
    private Integer size;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

    @OneToMany(mappedBy = "company")
    @JsonManagedReference
    private List<AddressCompany> addresses;

   @OneToMany(mappedBy = "company")
@JsonIgnore
private List<Job> jobs;

}