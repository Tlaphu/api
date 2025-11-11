package com.ra.base_spring_boot.dto.resp;


import lombok.Builder;
import lombok.Data;

@Data
@Builder // Sử dụng Builder để dễ dàng khởi tạo
public class PaymentResponse {


    private String paymentUrl;


    private String vnpayTxnRef;


    private String message;
}