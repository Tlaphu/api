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
public class LevelJob {
    @Id
    private String id;

    private String name;
    @Temporal(TemporalType.DATE)
    private Date created_at;
    @Temporal(TemporalType.DATE)
    private Date updated_at;

  @OneToMany(mappedBy = "levelJob", fetch = FetchType.EAGER)
private List<LevelJobRelation> levelJobRelations;
}