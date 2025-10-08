package com.ra.base_spring_boot.dto.resp;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EducationCandidateResponse {
    private Long id;
    private String nameEducation;
    private String major;
    private Date startedAt;
    private Date endAt;
    private String info;
    private Date createdAt;
    private Date updatedAt;
}
