package com.ra.base_spring_boot.dto.req;
import java.util.Date;
import lombok.Data;
import lombok.Getter;
@Data
@Getter 
public class FormJob {
               
    private String title;
    private String description;
    private Double salary;
    private String requirements;
    private String desirable;
    private String benefits;
    private Date expire_at;
    private long companyId; 
    private long locationId;
    
    private String companyName; 
    private long levelJobRelationsId;
    private String workTime; 
    private String status;   
}