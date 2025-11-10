package com.ra.base_spring_boot.dto.resp;

import lombok.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CandidateResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Integer gender;
    private Date dob;
    private String link;
    private boolean status;
    private Integer isOpen;
    private String Title;
    private String description;
    private String experience;
    private String development;

    private List<SkillsCandidateResponse> skills;
    private List<EducationCandidateResponse> educations;
    private List<ExperienceCandidateResponse> experiences;
    private List<CertificateCandidateResponse> certificates;
    private List<ProjectCandidateResponse> project;
    private List<JobCandidateResponse> jobs;
}
