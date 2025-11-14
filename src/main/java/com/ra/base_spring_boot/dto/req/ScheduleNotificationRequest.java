package com.ra.base_spring_boot.dto.req;

import lombok.Data;
import java.util.Date;

@Data
public class ScheduleNotificationRequest {
    private Long candidateId;
    private Long companyId;
    private String message;
    String companyName;
    String email;
    String address;
}
