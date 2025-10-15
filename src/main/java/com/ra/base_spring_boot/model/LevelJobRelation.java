package com.ra.base_spring_boot.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "levels_jobs")
public class LevelJobRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
@ManyToOne(fetch = FetchType.EAGER) 
@JoinColumn(name = "job_id", referencedColumnName = "id")

private Job job;

@ManyToOne(fetch = FetchType.EAGER) 
@JoinColumn(name = "level_id", referencedColumnName = "id")

private LevelJob levelJob;
}
