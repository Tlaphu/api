package com.ra.base_spring_boot.dto.resp;

import lombok.Data;

import java.util.Date;

@Data
public class JobCandidateResponse {

    private Long id;

    private Long jobId;
    private String jobTitle;

    private Long candidateId;
    private String candidateName;

    private String cv_url;
    private String cover_letter;
    private String status;

}
