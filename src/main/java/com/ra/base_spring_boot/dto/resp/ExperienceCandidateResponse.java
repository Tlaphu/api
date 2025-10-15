package com.ra.base_spring_boot.dto.resp;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienceCandidateResponse {
    private Long id;
    private String position;
    private String company;
    private Date started_at;
    private Date end_at;
    private String info;
    private Date created_at;
    private Date updated_at;
}

