package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class JobCandidate {
    @Id
    private String id;
    private String job_id;
    private String candidate_id;
    private String cv_url;
    private String content;
    private Integer status;
}
