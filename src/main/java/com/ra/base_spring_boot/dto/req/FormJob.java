package com.ra.base_spring_boot.dto.req;

import java.util.Date;
import java.util.Set;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FormJob {

    private String title;
    @Size(max = 1000, message = "Mô tả (Description) không được vượt quá 1000 ký tự.")
    private String description;
    private Double salary;

    private String desirable;
    @Size(max = 1000, message = "Phúc lợi (Benefits) không được vượt quá 1000 ký tự.")
    private String benefits;
    private Date expire_at;

    private Long locationId;

    private String companyName;
    private String workTime;
    private String status;


    private Long levelJobId;


    private Set<Long> skillIds;
}