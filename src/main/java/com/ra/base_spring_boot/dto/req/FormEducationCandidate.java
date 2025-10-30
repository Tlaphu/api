package com.ra.base_spring_boot.dto.req;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FormEducationCandidate {
    private Long id;
    private long candidateId;
    private String name_education;
    private String major;
    private String GPA;
    private Date startedAt;
    private Date endAt;
    private String info;
}

