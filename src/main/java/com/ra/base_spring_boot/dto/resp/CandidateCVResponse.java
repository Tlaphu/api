package com.ra.base_spring_boot.dto.resp;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCVResponse {
    // ... (Các trường cá nhân giữ nguyên)
    private Long id;
    private String name;
    private Date dob;
    private String address;
    private Integer gender;
    private String link;
    private String description;
    private String development;
    private String template;
    private String title;
    private String candidateTitle;
    private String email;
    private String phone;
    private String avatar;
    private String hobbies;

    // --- SKILLS ---
    private List<String> skills;

    // --- PROJECTS (Hoạt động) ---
    private List<String> projects;          // Tên dự án (ProjectCandidateNames)
    private List<String> projectLinks;
    private List<String> projectInfos;

    // NEW FIELDS
    private List<String> projectStartDates; // Ngày bắt đầu Project
    private List<String> projectEndDates;   // Ngày kết thúc Project

    // --- EDUCATIONS (Học vấn) ---
    private List<String> educations;        // Tên trường (EducationCandidateNames)
    private List<String> educationMajors;
    private List<String> educationGPAs;
    private List<String> educationInfos;

    // NEW FIELDS
    private List<String> educationStartDates; // Ngày bắt đầu Education
    private List<String> educationEndDates;   // Ngày kết thúc Education


    private List<String> experiences;
    private List<String> experienceCompanies;
    private List<String> experiencePositions;
    private List<String> experienceInfos;

    // NEW FIELDS
    private List<String> experienceStartDates; // Ngày bắt đầu Experience
    private List<String> experienceEndDates;   // Ngày kết thúc Experience


    private List<String> certificates;
    private List<String> certificateOrganizations;
    private List<String> certificateInfos;

    // NEW FIELDS
    private List<String> certificateStartDates; // Ngày cấp Certificate
    private List<String> certificateEndDates;   // Ngày hết hạn Certificate (hoặc Năm cấp)
}