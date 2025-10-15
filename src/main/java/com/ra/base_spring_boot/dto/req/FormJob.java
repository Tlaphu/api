package com.ra.base_spring_boot.dto.req;
import java.util.Date;
import lombok.Data;

@Data
public class FormJob {
               
    private String title;
    private String description;
    private Double salary;
    private String requirements;
    private String desirable;
    private String benefits;
    private Date expire_at;
    private Long companyId;
    private Long locationId;
    private String workTime;     
}