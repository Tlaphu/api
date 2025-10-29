package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CandidateCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore
    private Candidate candidate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillsCandidate> skillCandidates;

    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCandidate> projectCandidates;

    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EducationCandidate> educationCandidates;

    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceCandidate> experienceCandidates;

    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateCandidate> certificateCandidates;
}
