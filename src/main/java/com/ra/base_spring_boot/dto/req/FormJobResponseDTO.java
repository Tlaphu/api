package com.ra.base_spring_boot.dto.req;

import java.util.Date;
import java.util.List;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormJobResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Double salary;
    private String requirements;
    private String desirable;
    private String benefits;
    private String workTime;
    private Long locationId;
    private String companyName;
    private String companyLogo;


    private String levelJobName;


    private List<String> skillNames;

    private Date created_at;
    private Date expire_at;
    private String status;
}