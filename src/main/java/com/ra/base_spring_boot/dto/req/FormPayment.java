package com.ra.base_spring_boot.dto.req;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class FormPayment {
    @NotNull
    private Long candidateId;

    @NotNull
    private Long planId;
}
