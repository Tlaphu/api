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
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    private Integer isOpen;

    @Temporal(TemporalType.DATE)
    private Date dob;

    private String address;
    @Column(unique = true, nullable = false)
    private String email;
    private String phone;
    private String password;
    @Builder.Default
    private boolean status = false;
    private String verificationToken;
    @Column(unique = true) 
    private String resetToken;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "candidate_roles",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
    @ManyToMany
    @JoinTable(
        name = "candidate_favorite_jobs", 
        joinColumns = @JoinColumn(name = "candidate_id"),
        inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    @Builder.Default
    private Set<Job> favoriteJobs = new HashSet<>();

    private Integer gender;

    private String link;
    private String Description;
    private String Experience;
    private String Development;
    private String Title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectCandidate> projectCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SkillsCandidate> skillCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EducationCandidate> educationCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ExperienceCandidate> experienceCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<CertificateCandidate> certificateCandidates;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<JobCandidate> jobCandidates;
}
