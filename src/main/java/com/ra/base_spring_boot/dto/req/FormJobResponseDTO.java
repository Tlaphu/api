package com.ra.base_spring_boot.dto.req;

import java.util.Date;

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
    private Long companyId;
    private Long locationId;
    private String companyName;
    private String companyLogo;
    private String locationName;
    private String levelJobName;
    private Date created_at;
    private Date expire_at;
    private String status;
}
