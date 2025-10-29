package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SkillsCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonBackReference
    private Candidate candidate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_cv_id")
    @JsonIgnore
    private CandidateCV candidateCV;

    private String name;
    private String level_job_id;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}