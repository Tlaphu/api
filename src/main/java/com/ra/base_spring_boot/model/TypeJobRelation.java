package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "types_jobs")
public class TypeJobRelation {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    @JsonIgnore 
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_job_id", referencedColumnName = "id")
    @JsonIgnore 
    private TypeJob typeJob;
}
