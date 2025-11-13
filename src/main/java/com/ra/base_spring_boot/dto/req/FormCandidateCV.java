package com.ra.base_spring_boot.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormCandidateCV {

    private String title;
    private String template;
    private String name;
    private Date dob; // Cần parse '15/05/1995' thành Date
    private String email;
    private String phone;
    private String address;
    private String link;

    private String description;
    private String development;

    private String candidateTitle;
    private String avatar;
    private String gender;
    private String hobbies;
    private List<FormCertificateCandidate> certificates;
    private List<FormExperienceCandidate> experiences;
    private List<FormEducationCandidate> educations;
    private List<FormSkillCandidate> skills;
    private List<FormProjectCandidate> projects;
}

