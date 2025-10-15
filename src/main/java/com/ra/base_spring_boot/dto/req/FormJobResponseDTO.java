package com.ra.base_spring_boot.dto.req;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormJobResponseDTO {
    private long id;
    private String title;
    private String description;
    private Double salary;
    private String requirements;
    private String desirable;
    private String benefits;
    private String workTime;
    private String companyName;
    private String companyLogo;
    private String locationName;
}
