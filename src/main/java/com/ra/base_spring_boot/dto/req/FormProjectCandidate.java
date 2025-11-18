package com.ra.base_spring_boot.dto.req;

import lombok.*;
import java.util.Date;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class FormProjectCandidate {
    private Long id;
    String name;
    String link;
    private Date started_at;
    private Date end_at;
    private String info;


}
