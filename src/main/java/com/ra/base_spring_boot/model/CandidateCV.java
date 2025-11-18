package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateCV {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String template;
    private String title;

    @Column(name = "candidate_title")
    private String candidateTitle;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidate_id", nullable = false)
    @JsonIgnore
    private Candidate candidate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created_at;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updated_at;

    private String name;
    private Date dob;
    private String email;
    private String phone;
    private String address;
    private String link;
    private String description;
    private String development;
    private String avatar;
    private String hobbies;
    private Integer gender;

    // ... (Phần Skill giữ nguyên)
    @Column(name = "skill_candidate_ids", columnDefinition = "TEXT")
    private String skillCandidateIds;
    @Column(name = "skill_candidate_names", columnDefinition = "TEXT")
    private String skillCandidateNames;

    // --- Cập nhật cho Education ---
    @Column(name = "education_candidate_ids", columnDefinition = "TEXT")
    private String educationCandidateIds;
    @Column(name = "education_candidate_names", columnDefinition = "TEXT")
    private String educationCandidateNames;
    @Column(name = "education_candidate_gpa")
    private String educationCandidateGPA;
    @Column(name = "education_candidate_major", columnDefinition = "TEXT")
    private String educationCandidateMajor;
    @Column(name = "eductaion_candidate_info", columnDefinition = "TEXT")
    private String educationCandidateInfo;

    // TRƯỜNG MỚI: Thời gian bắt đầu Education
    @Column(name = "education_candidate_start_dates", columnDefinition = "TEXT")
    private String educationCandidateStartDates;

    // TRƯỜNG MỚI: Thời gian kết thúc Education
    @Column(name = "education_candidate_end_dates", columnDefinition = "TEXT")
    private String educationCandidateEndDates;


    // --- Cập nhật cho Experience ---
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

    // TRƯỜNG MỚI: Thời gian bắt đầu Experience
    @Column(name = "experience_candidate_start_dates", columnDefinition = "TEXT")
    private String experienceCandidateStartDates;

    // TRƯỜNG MỚI: Thời gian kết thúc Experience
    @Column(name = "experience_candidate_end_dates", columnDefinition = "TEXT")
    private String experienceCandidateEndDates;


    // --- Cập nhật cho Certificate ---
    @Column(name = "certificate_candidate_ids", columnDefinition = "TEXT")
    private String certificateCandidateIds;
    @Column(name = "certificate_candidate_names", columnDefinition = "TEXT")
    private String certificateCandidateNames;
    @Column(name = "certificate_candidate_organization", columnDefinition = "TEXT")
    private String certificateCandidateOrganization;
    @Column(name = "certificate_candidate_info", columnDefinition = "TEXT")
    private String certificateCandidateInfo;

    // TRƯỜNG MỚI: Thời gian bắt đầu Certificate
    @Column(name = "certificate_candidate_start_dates", columnDefinition = "TEXT")
    private String certificateCandidateStartDates;

    // TRƯỜNG MỚI: Thời gian kết thúc Certificate
    @Column(name = "certificate_candidate_end_dates", columnDefinition = "TEXT")
    private String certificateCandidateEndDates;


    // --- Cập nhật cho Project ---
    @Column(name = "project_candidate_ids", columnDefinition = "TEXT")
    private String projectCandidateIds;
    @Column(name = "project_candidate_names", columnDefinition = "TEXT")
    private String projectCandidateNames;
    @Column(name = "project_candidate_link", columnDefinition = "TEXT")
    private String projectCandidateLink;
    @Column(name = "project_candidate_info", columnDefinition = "TEXT")
    private String projectCandidateInfo;


    @Column(name = "project_candidate_start_dates", columnDefinition = "TEXT")
    private String projectCandidateStartDates;


    @Column(name = "project_candidate_end_dates", columnDefinition = "TEXT")
    private String projectCandidateEndDates;
}