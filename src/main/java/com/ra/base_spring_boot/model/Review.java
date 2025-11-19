package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "reviews")
@Builder
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int score;
    private String detail;

    private Long reviewerId;      // ai review
    private String reviewerType;  // CANDIDATE / COMPANY

    private Date createdAt;
}

