package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "skills")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @Temporal(TemporalType.DATE)
    private Date createdAt ;

    @Temporal(TemporalType.DATE)
    private Date updatedAt ;

    @OneToMany(mappedBy = "skill", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<SkillsCandidate> skillsCandidates;

    @ManyToMany(mappedBy = "skills")
    @JsonIgnore
    private Set<Job> jobs;
}
