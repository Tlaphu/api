package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LevelJobRelation {
    @Id
    private String id;
    private String job_id;
    private String level_id;
}
