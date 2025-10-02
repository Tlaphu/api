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
public class EducationCandidate {
    @Id
    private String id;
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    
    private String name_educatuon;
    private String major;
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