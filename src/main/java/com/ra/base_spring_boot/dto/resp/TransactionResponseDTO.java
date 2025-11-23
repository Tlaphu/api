package com.ra.base_spring_boot.dto.resp;

import lombok.Getter;
import lombok.Setter; // Cần thiết để tạo ra tất cả các phương thức set
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

// THÊM CÁC ANNOTATION NÀY VÀO DTO CỦA BẠN:
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponseDTO {

    private Long id;
    private BigDecimal amount;
    private String bankCode;
    private String transactionStatus;
    private String vnpayOrderInfo;
    private String vnpayTxnRef;
    private Date paymentDate;


    private String subscriptionPlanName;
    private Integer subscriptionDurationDays;


    private String userType;
    private String userIdentifier;
    private Long userId;

}