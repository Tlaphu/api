package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String template;
    private String title;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore
    private Candidate candidate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    private String name;
    private Date dob;
    private String email;
    private String phone;
    private String address;
    private String link;
    private String description;
    private String development;


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL)
    @Default
    private List<SkillsCandidate> skillCandidates = new ArrayList<>();


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL)
    @Default
    private List<ProjectCandidate> projectCandidates = new ArrayList<>();


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL)
    @Default
    private List<EducationCandidate> educationCandidates = new ArrayList<>();


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL)
    @Default
    private List<ExperienceCandidate> experienceCandidates = new ArrayList<>();


    @OneToMany(mappedBy = "candidateCV", cascade = CascadeType.ALL)
    @Default
    private List<CertificateCandidate> certificateCandidates = new ArrayList<>();
}