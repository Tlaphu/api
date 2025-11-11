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
    private String skillCandidateNames;

    private String educationCandidateIds;
    private String educationCandidateNames;
    private String educationCandidateGPA;
    private String eductaionCandidateInfo;
    private String educationCandidateMajor;
    private String experienceCandidateIds;
    private String experienceCandidateNames;
    private String experienceCandidateCompany;
    private String experienceCandidateInfo;
    private String experienceCandidatePosition;
    private String certificateCandidateIds;
    private String certificateCandidateNames;
    private String certificateCandidateInfo;
    private String certificateCandidateOrganization;
    private String projectCandidateIds;
    private String projectCandidateNames;
    private String projectCandidateInfo;
    private String projectCandidateLink;










}