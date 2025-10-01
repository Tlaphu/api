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
public class Candidate {
    @Id
    private String id;

    private String name;
    private Integer isOpen;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private String address;
    private String email;
    private String phone;
    private String password;
    private Integer gender;
    private String link_fb;
    private String link_linkeidn;
    private String link_git;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}