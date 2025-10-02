package com.ra.base_spring_boot.model;

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
public class Candidate {
    @Id
    private String id;

    private String name;
    @Column(columnDefinition = "TEXT")
    private String isOpen;
    @Temporal(TemporalType.DATE)
    private Date dob;
    private String address;
    private String email;
    private String phone;
    private String password;
    private String gender;
    private String link_fb;
    private String link_linkedin;
    private String link_gg;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

    @OneToMany(mappedBy = "candidate")
    private List<ProjectCandidate> projectCandidates;

    @OneToMany(mappedBy = "candidate")
    private List<SkillsCandidate> skillsCandidates;

    @OneToMany(mappedBy = "candidate")
    private List<EducationCandidate> educationCandidates;

    @OneToMany(mappedBy = "candidate")
    private List<ExperienceCandidate> experienceCandidates;

    @OneToMany(mappedBy = "candidate")
    private List<CertificateCandidate> certificateCandidates;

    @OneToMany(mappedBy = "candidate")
    private List<JobCandidate> jobCandidates;
}