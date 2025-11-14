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

    // --- THÔNG TIN CÁ NHÂN (Giữ nguyên) ---
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

    // TRƯỜNG MỚI: Tên kỹ năng
    @Column(name = "skill_candidate_names", columnDefinition = "TEXT")
    private String skillCandidateNames;

    // --- 2. EDUCATION ---
    @Column(name = "education_candidate_ids", columnDefinition = "TEXT")
    private String educationCandidateIds;

    // TRƯỜNG MỚI: Tên trường
    @Column(name = "education_candidate_names", columnDefinition = "TEXT")
    private String educationCandidateNames;

    // TRƯỜNG MỚI: GPA
    @Column(name = "education_candidate_gpa")
    private String educationCandidateGPA;

    // TRƯỜNG MỚI: Ngành học
    @Column(name = "education_candidate_major", columnDefinition = "TEXT")
    private String educationCandidateMajor;

    // TRƯỜNG MỚI: Info (Lưu ý: giữ nguyên lỗi chính tả 'eductaion' để khớp với Service/Form hiện tại)
    @Column(name = "eductaion_candidate_info", columnDefinition = "TEXT")
    private String eductaionCandidateInfo;

    // --- 3. EXPERIENCE ---
    @Column(name = "experience_candidate_ids", columnDefinition = "TEXT")
    private String experienceCandidateIds;

    // TRƯỜNG MỚI: Tên (Pos @ Comp)
    @Column(name = "experience_candidate_names", columnDefinition = "TEXT")
    private String experienceCandidateNames;

    // TRƯỜNG MỚI: Công ty
    @Column(name = "experience_candidate_company", columnDefinition = "TEXT")
    private String experienceCandidateCompany;

    // TRƯỜNG MỚI: Mô tả
    @Column(name = "experience_candidate_info", columnDefinition = "TEXT")
    private String experienceCandidateInfo;

    // TRƯỜNG MỚI: Vị trí
    @Column(name = "experience_candidate_position", columnDefinition = "TEXT")
    private String experienceCandidatePosition;

    // --- 4. CERTIFICATE ---
    @Column(name = "certificate_candidate_ids", columnDefinition = "TEXT")
    private String certificateCandidateIds;

    // TRƯỜNG MỚI: Tên chứng chỉ
    @Column(name = "certificate_candidate_names", columnDefinition = "TEXT")
    private String certificateCandidateNames;

    // TRƯỜNG MỚI: Tổ chức
    @Column(name = "certificate_candidate_organization", columnDefinition = "TEXT")
    private String certificateCandidateOrganization;

    // TRƯỜNG MỚI: Mô tả chứng chỉ
    @Column(name = "certificate_candidate_info", columnDefinition = "TEXT")
    private String certificateCandidateInfo;

    // --- 5. PROJECT ---
    @Column(name = "project_candidate_ids", columnDefinition = "TEXT")
    private String projectCandidateIds;

    // TRƯỜNG MỚI: Tên dự án
    @Column(name = "project_candidate_names", columnDefinition = "TEXT")
    private String projectCandidateNames;

    // TRƯỜNG MỚI: Link dự án
    @Column(name = "project_candidate_link", columnDefinition = "TEXT")
    private String projectCandidateLink;

    // TRƯỜNG MỚI: Mô tả dự án
    @Column(name = "project_candidate_info", columnDefinition = "TEXT")
    private String projectCandidateInfo;

    // --- TIMESTAMP (Giữ nguyên) ---

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