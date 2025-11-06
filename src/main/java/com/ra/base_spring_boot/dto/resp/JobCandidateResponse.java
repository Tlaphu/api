package com.ra.base_spring_boot.dto.resp;

import com.fasterxml.jackson.core.JsonToken;
import lombok.Data;
import lombok.*;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Data
public class JobCandidateResponse {

    private Long id;

    private Long jobId;
    private String jobTitle;
    private String jobLocationId;
    private Long candidateId;
    private String candidateName;
    private String candidateTitle;
    private Long skillcandidateId;


    private Long cvId;
    private String cover_letter;
    private String status;


}
