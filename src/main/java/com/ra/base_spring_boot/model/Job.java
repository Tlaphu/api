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
public class Job {
    @Id
    private String id;
    private String title;
    private String company_id;
    private String description;
    private String salary;
    private String location_id;
    @Temporal(TemporalType.DATE)
    private Date expire_at;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}
