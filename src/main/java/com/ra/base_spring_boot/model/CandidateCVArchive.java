package com.ra.base_spring_boot.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "candidate_cv_archive")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCVArchive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;

    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link; // Link LinkedIn/CV/etc.

    @Column(name = "development", columnDefinition = "TEXT")
    private String development; // Mục tiêu nghề nghiệp

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;


    @Column(name = "candidate_cv_id", nullable = false)
    private Long candidateCVId;

    @Column(nullable = false)
    private String title;

    // Các trường ID chi tiết
    @Column(name = "skill_candidate_ids", columnDefinition = "TEXT")
    private String skillCandidateIds;

    @Column(name = "education_candidate_ids", columnDefinition = "TEXT")
    private String educationCandidateIds;

    @Column(name = "experience_candidate_ids", columnDefinition = "TEXT")
    private String experienceCandidateIds;

    @Column(name = "certificate_candidate_ids", columnDefinition = "TEXT")
    private String certificateCandidateIds;

    @Column(name = "project_candidate_ids", columnDefinition = "TEXT")
    private String projectCandidateIds;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Date();
        updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = new Date();
    }
}