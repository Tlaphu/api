package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
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
    @Size(max = 36)
    @Column(length = 36)
    private String detail;

    private Long reviewerId;
    private String reviewerType;

    private Date createdAt;
}

