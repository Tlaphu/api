package com.ra.base_spring_boot.dto.resp;

import lombok.*;

import java.util.Date;

@Builder
@Getter @Setter
public class ReviewResponse {
    private Long id;
    private int score;
    private String detail;
    private Date createdAt;

    private String reviewerName;
    private String reviewerLogo;
    private String reviewerType;

}


