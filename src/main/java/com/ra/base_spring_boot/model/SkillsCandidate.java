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
public class SkillsCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id; 
    private String name;
    @JsonIgnore 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false) 
    private Candidate candidate; 
    @ManyToOne
    @JsonIgnore 
    @JoinColumn(name = "cv_id", nullable = true) 
    private CandidateCV candidateCV;
    @ManyToOne
    @JsonIgnore 
    @JoinColumn(name = "level_job_id") 
    private LevelJob levelJob;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;
}