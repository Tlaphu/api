package com.ra.base_spring_boot.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Date;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormCandidateCV {

    private String title;
    private String template;
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date dob;
    private String email;
    private String phone;
    private String address;
    private String link;
    private Integer gender;
    private String description;
    private String development;

    private String candidateTitle;
    private String avatar;

    private String hobbies;
    private List<FormCertificateCandidate> certificates;
    private List<FormExperienceCandidate> experiences;
    private List<FormEducationCandidate> educations;
    private List<FormSkillCandidate> skills;
    private List<FormProjectCandidate> projects;
}

