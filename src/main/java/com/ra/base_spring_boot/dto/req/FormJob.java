package com.ra.base_spring_boot.dto.req;

import java.util.Date;
import java.util.Set;
import lombok.Data;

@Data
public class FormJob {

    private String title;
    private String description;
    private Double salary;

    private String desirable;
    private String benefits;
    private Date expire_at;

    private Long locationId;
    private String companyName;
    private String workTime;
    private String status;


    private Long levelJobId;


    private Set<Long> skillIds;
}