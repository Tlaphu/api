package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ra.base_spring_boot.model.base.BaseObject;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Candidate extends BaseObject {

    private String name;

    private Integer isOpen;

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String address;
    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "candidate_roles",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();


    private Integer gender;

    private String link_fb;
    private String link_linkedin;
    private String link_git;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCandidate> projectCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SkillsCandidate> skillCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EducationCandidate> educationCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceCandidate> experienceCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CertificateCandidate> certificateCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<JobCandidate> jobCandidates;
}
