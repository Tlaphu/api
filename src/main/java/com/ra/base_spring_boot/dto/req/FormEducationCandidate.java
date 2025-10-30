package com.ra.base_spring_boot.dto.req;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormEducationCandidate {
    private String nameEducation;
    private String major;
    private String gpa;
    private Date startedAt;
    private Date endAt;
    private String info;
}


