package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillsCandidateResponse {
    private Long id;
    private String name;
    private String level_job_id;
    private Date created_at;
    private Date updated_at;
}
