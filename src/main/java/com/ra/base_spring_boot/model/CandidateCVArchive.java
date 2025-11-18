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

    // --- THÔNG TIN CÁ NHÂN ---
    @Column(name = "candidate_name", nullable = false)
    private String candidateName;
    @Column(name = "candidate_title")
    private String candidateTitle;
    @Temporal(TemporalType.DATE)
    @Column(name = "dob")
    private Date dob;
    private String hobbies;
    @Column(name = "email")
    private String email;
    private Integer gender;
    @Column(name = "phone")
    private String phone;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "link", columnDefinition = "TEXT")
    private String link;

    @Column(name = "development", columnDefinition = "TEXT")
    private String development;

    @Column(name = "candidate_id", nullable = false)
    private Long candidateId;

    @Column(name = "candidate_cv_id", nullable = false)
    private Long candidateCVId;

    @Column(nullable = false)
    private String title;

    // --- 1. SKILLS ---
    @Column(name = "skill_candidate_ids", columnDefinition = "TEXT")
    private String skillCandidateIds;
    @Column(name = "skill_candidate_names", columnDefinition = "TEXT")
    private String skillCandidateNames;

    // --- 2. EDUCATION ---
    @Column(name = "education_candidate_ids", columnDefinition = "TEXT")
    private String educationCandidateIds;
    @Column(name = "education_candidate_names", columnDefinition = "TEXT")
    private String educationCandidateNames;
    @Column(name = "education_candidate_gpa")
    private String educationCandidateGPA;
    @Column(name = "education_candidate_major", columnDefinition = "TEXT")
    private String educationCandidateMajor;
    @Column(name = "eductaion_candidate_info", columnDefinition = "TEXT")
    private String eductaionCandidateInfo;

    // NEW: THỜI GIAN EDUCATION
    @Column(name = "education_candidate_start_dates", columnDefinition = "TEXT")
    private String educationCandidateStartDates;
    @Column(name = "education_candidate_end_dates", columnDefinition = "TEXT")
    private String educationCandidateEndDates;


    // --- 3. EXPERIENCE ---
    @Column(name = "experience_candidate_ids", columnDefinition = "TEXT")
    private String experienceCandidateIds;
    @Column(name = "experience_candidate_names", columnDefinition = "TEXT")
    private String experienceCandidateNames;
    @Column(name = "experience_candidate_company", columnDefinition = "TEXT")
    private String experienceCandidateCompany;
    @Column(name = "experience_candidate_info", columnDefinition = "TEXT")
    private String experienceCandidateInfo;
    @Column(name = "experience_candidate_position", columnDefinition = "TEXT")
    private String experienceCandidatePosition;

    // NEW: THỜI GIAN EXPERIENCE
    @Column(name = "experience_candidate_start_dates", columnDefinition = "TEXT")
    private String experienceCandidateStartDates;
    @Column(name = "experience_candidate_end_dates", columnDefinition = "TEXT")
    private String experienceCandidateEndDates;


    // --- 4. CERTIFICATE ---
    @Column(name = "certificate_candidate_ids", columnDefinition = "TEXT")
    private String certificateCandidateIds;
    @Column(name = "certificate_candidate_names", columnDefinition = "TEXT")
    private String certificateCandidateNames;
    @Column(name = "certificate_candidate_organization", columnDefinition = "TEXT")
    private String certificateCandidateOrganization;
    @Column(name = "certificate_candidate_info", columnDefinition = "TEXT")
    private String certificateCandidateInfo;

    // NEW: THỜI GIAN CERTIFICATE
    @Column(name = "certificate_candidate_start_dates", columnDefinition = "TEXT")
    private String certificateCandidateStartDates;
    @Column(name = "certificate_candidate_end_dates", columnDefinition = "TEXT")
    private String certificateCandidateEndDates;


    // --- 5. PROJECT ---
    @Column(name = "project_candidate_ids", columnDefinition = "TEXT")
    private String projectCandidateIds;
    @Column(name = "project_candidate_names", columnDefinition = "TEXT")
    private String projectCandidateNames;
    @Column(name = "project_candidate_link", columnDefinition = "TEXT")
    private String projectCandidateLink;
    @Column(name = "project_candidate_info", columnDefinition = "TEXT")
    private String projectCandidateInfo;

    // NEW: THỜI GIAN PROJECT
    @Column(name = "project_candidate_start_dates", columnDefinition = "TEXT")
    private String projectCandidateStartDates;
    @Column(name = "project_candidate_end_dates", columnDefinition = "TEXT")
    private String projectCandidateEndDates;


    // --- TIMESTAMP ---
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