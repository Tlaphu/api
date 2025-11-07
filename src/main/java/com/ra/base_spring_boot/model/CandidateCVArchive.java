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

    // CHỈ LƯU ID - KHÔNG DÙNG @ManyToOne VÀ @JoinColumn
    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    // CHỈ LƯU ID - KHÔNG DÙNG @ManyToOne VÀ @JoinColumn
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