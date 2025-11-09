package com.ra.base_spring_boot.dto.req;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormCandidateCVArchive {


    private String title;


    private String skillCandidateIds;
    private String educationCandidateIds;
    private String experienceCandidateIds;
    private String certificateCandidateIds;
    private String projectCandidateIds;
}