package com.ra.base_spring_boot.dto.resp;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SkillCandidateResponse {
    private Long id;
    private String name;
    private String level_job_id;
}