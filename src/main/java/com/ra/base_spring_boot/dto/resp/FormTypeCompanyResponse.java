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
private String id;
    private String name;
    
    // Thêm thông tin thời gian để client biết khi nào loại hình này được tạo/cập nhật
    private Date createdAt; 
    private Date updatedAt; 
}
