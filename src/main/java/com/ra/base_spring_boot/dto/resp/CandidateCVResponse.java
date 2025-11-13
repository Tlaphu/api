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
    private List<String> skills; // (Nội dung Skill - Giữ nguyên)

    // --- PROJECTS (Hoạt động) ---
    private List<String> projects; // Tên dự án (ProjectCandidateNames)
    private List<String> projectLinks;       // Link dự án (ProjectCandidateLink)
    private List<String> projectInfos;       // Thông tin chi tiết + Thời gian (ProjectCandidateInfo)

    // --- EDUCATIONS (Học vấn) ---
    private List<String> educations; // Tên trường (EducationCandidateNames)
    private List<String> educationMajors;    // Ngành học (EducationCandidateMajor)
    private List<String> educationGPAs;      // GPA/Điểm số (EducationCandidateGPA)
    private List<String> educationInfos;     // Thông tin chi tiết + Thời gian (EductaionCandidateInfo)


    private List<String> experiences;
    private List<String> experienceCompanies;
    private List<String> experiencePositions;
    private List<String> experienceInfos;


    private List<String> certificates;
    private List<String> certificateOrganizations;
    private List<String> certificateInfos;
}