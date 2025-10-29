package com.ra.base_spring_boot.dto.req;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class FormJobCandidate {

    @NotNull(message = "Job ID cannot be null")
    private Long jobId;

    @NotNull(message = "Candidate ID cannot be null")
    private Long candidateId;

    @NotBlank(message = "CV URL is required")
    private String cvUrl;

    private String coverLetter;

    private String status;
}
