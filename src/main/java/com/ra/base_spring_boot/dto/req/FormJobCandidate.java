package com.ra.base_spring_boot.dto.req;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class FormJobCandidate {

    @NotNull(message = "Job ID cannot be null")
    private Long jobId;

    
    private Long candidateId;

    private Long cvid;

    private String coverLetter;

    private String status;
}
