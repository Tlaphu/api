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
    private String nameEducation;
    private String major;
    private String gpa;
    private Date startedAt;
    private Date endAt;
    private String info;
}


