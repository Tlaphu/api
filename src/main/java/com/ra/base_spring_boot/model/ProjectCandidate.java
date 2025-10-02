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
public class ProjectCandidate {
    @Id
    private String id;
    private String candidate_id;
    private String name;
    private String link;
    @Temporal(TemporalType.DATE)
    private Date started_at;
    @Temporal(TemporalType.DATE)
    private Date end_at;
    private String info;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}