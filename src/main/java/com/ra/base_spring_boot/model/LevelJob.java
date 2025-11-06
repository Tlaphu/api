package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.List;
import java.util.Set;
@Table(name = "leveljob") 
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LevelJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;

    @Temporal(TemporalType.DATE)
    private Date created_at;

    @Temporal(TemporalType.DATE)
    private Date updated_at;


    @OneToMany(mappedBy = "levelJob", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LevelJobRelation> levelJobRelations;
    @OneToMany(mappedBy = "levelJob", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore 
    private Set<SkillsCandidate> skillCandidates; 
}
