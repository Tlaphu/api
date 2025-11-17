package com.ra.base_spring_boot.dto.req;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class FormPayment {
    @NotNull
    private Long accountId; // ID của Candidate HOẶC AccountCompany

    @NotNull
    private Long planId; // ID của SubscriptionPlan

    @NotNull
    private String accountType;
}
