package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private String type;
    private String redirectUrl;
    private boolean isRead;
    private Date createdAt;

    private String companyLogo;
}
