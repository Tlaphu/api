package com.ra.base_spring_boot.dto.req;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormSkillCandidate {
    private Long id;
    private Long skillId;
    private Long levelJobId;
    private String skillName;
}
