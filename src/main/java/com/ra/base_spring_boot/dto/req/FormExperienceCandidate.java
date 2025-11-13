package com.ra.base_spring_boot.dto.req;

import lombok.*;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class FormExperienceCandidate {
    private Long id;
    private String position;
    private String company;
    private List<String> description;

    private Date started_at;
    private Date end_at;
    private String info;
}
