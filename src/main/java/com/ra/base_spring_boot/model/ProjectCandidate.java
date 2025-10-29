package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProjectCandidate {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false) 
    private Candidate candidate; 
   @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_id", nullable = true) 
    @JsonIgnore 
    private CandidateCV candidateCV;
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