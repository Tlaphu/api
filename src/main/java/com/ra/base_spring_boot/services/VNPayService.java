package com.ra.base_spring_boot.services;

import com.ra.base_spring_boot.dto.resp.TransactionResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public interface VNPayService {


    String createPaymentUrl(HttpServletRequest request, BigDecimal amount, String orderInfo, String vnpayTxnRef) throws UnsupportedEncodingException;


    boolean handleVNPayReturn(Map<String, String> params);
    List<TransactionResponseDTO> getAllTransactionsForAdmin();
}