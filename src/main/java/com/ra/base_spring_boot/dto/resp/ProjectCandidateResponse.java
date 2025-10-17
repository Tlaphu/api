package com.ra.base_spring_boot.dto.resp;

import java.util.Date;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCandidateResponse {
    private String id;
    private String name;
    private String link;
    private Date started_at;
    private Date end_at;
    private String info;
    private Date created_at;
    private Date updated_at;
}
