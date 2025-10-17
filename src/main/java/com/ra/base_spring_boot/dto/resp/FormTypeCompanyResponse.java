package com.ra.base_spring_boot.dto.resp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTypeCompanyResponse {
private Long  id;
    private String name;
    
    
    private Date createdAt; 
    private Date updatedAt; 
}
