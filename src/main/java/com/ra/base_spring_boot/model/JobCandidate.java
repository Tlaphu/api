package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobCandidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    @JsonBackReference
    private Job job;

    @ManyToOne
    @JoinColumn(name = "candidate_id")
    @JsonBackReference
    private Candidate candidate;
    @ManyToOne
    @JoinColumn(name = "cv_id", nullable = true)
    @JsonBackReference
    private CandidateCV candidateCV;
    
    private String cover_letter;
    private String status;
    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

}
