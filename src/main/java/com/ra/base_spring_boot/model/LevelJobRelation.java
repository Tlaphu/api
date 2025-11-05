package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_level_relations")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LevelJobRelation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "job_id")
    @JsonIgnore
    private Job job;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "level_job_id")
    @JsonIgnore
    private LevelJob levelJob;
}
