package com.ra.base_spring_boot.dto.req;

import lombok.Data;
import java.util.Date;

@Data
public class FormUpdateAccountCompany {
    private String fullName;
    private String phone;
    private Date dob;
    private Integer gender;
}
