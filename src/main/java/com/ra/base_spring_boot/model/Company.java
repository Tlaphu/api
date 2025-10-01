package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Company {
    @Id
    private String id;
    private String account_company_id;
    private String name;
    private String logo;
    private String website;
    private String link_fb;
    private String link_linkeidn;
    private Integer follower;
    private Integer size;
    private String typeCompany_id;
    private String description;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}