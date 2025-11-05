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
    private String skillName;
    private String levelJobName;
    private Date createdAt;
    private Date updatedAt;
}
