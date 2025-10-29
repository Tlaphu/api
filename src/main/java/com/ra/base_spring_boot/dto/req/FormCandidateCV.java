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

    
    private List<FormCertificateCandidate> certificates;
    private List<FormExperienceCandidate> experiences;
    private List<FormEducationCandidate> educations;
    private List<FormSkillsCandidate> skills;
    private List<FormExperienceCandidate> projects;
}

