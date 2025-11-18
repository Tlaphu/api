package com.ra.base_spring_boot.dto.req;

import lombok.Data;

@Data
public class ReviewUpdateRequest {

    private int score;

    private String detail;
}
