package com.ra.base_spring_boot.dto.req;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormTypeCompany {
 @NotBlank(message = "ID cannot be blank")
    @Size(max = 50, message = "ID must be less than 50 characters")
    private String id;
    
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 100, message = "Name must be less than 100 characters")
    private String name;
}
