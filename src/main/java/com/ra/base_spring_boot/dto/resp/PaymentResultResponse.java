package com.ra.base_spring_boot.dto.resp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResultResponse {
    private boolean success;
    private String transactionStatus;
    private Date newPremiumUntil;
    private String userType;
}
