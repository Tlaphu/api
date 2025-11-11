package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.config.VNPayProperties;
import com.ra.base_spring_boot.config.VNPayUtil;
import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.PaymentTransaction;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.PaymentTransactionRepository;
import com.ra.base_spring_boot.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayServiceImpl implements VNPayService {

    private final VNPayProperties vnpayProperties;
    private final ICandidateRepository candidateRepository;
    private final PaymentTransactionRepository transactionRepository;



    @Override
    public String createPaymentUrl(HttpServletRequest request, BigDecimal amount, String orderInfo, String vnpayTxnRef) throws UnsupportedEncodingException {

        long vnp_Amount = amount.multiply(new BigDecimal(100)).longValue();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");


        vnp_Params.put("vnp_TxnRef", vnpayTxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());

        String vnp_IpAddr = VNPayUtil.getIpAddress(request);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);


        Map<String, String> sorted_vnp_Params = VNPayUtil.sortParams(vnp_Params);
        String hashData = VNPayUtil.buildHashData(sorted_vnp_Params);
        System.out.println("HASH DATA TO BE SIGNED: " + hashData);
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnpayProperties.getHashSecret(), hashData);


        String queryUrl = VNPayUtil.createQueryUrl(sorted_vnp_Params);

        String paymentUrl = vnpayProperties.getUrl() + "?" + queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;

        return paymentUrl;
    }



    @Override
    @Transactional
    public boolean handleVNPayReturn(Map<String, String> params) {

        String vnp_SecureHash = params.get("vnp_SecureHash");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");


        params.remove("vnp_SecureHash");


        Map<String, String> sortedParams = VNPayUtil.sortParams(params);
        String hashData;
        try {
            hashData = VNPayUtil.buildHashData(sortedParams);
        } catch (UnsupportedEncodingException e) {

            return false;
        }
        String generatedHash = VNPayUtil.hmacSHA512(vnpayProperties.getHashSecret(), hashData);


        if (!generatedHash.equals(vnp_SecureHash)) {

            return false;
        }


        PaymentTransaction transaction = transactionRepository.findByVnpayTxnRef(vnp_TxnRef)
                .orElse(null);

        if (transaction == null) {

            return false;
        }

        if ("00".equals(vnp_ResponseCode)) {

            transaction.setTransactionStatus("SUCCESS");
            transaction.setPaymentDate(new Date());
            transactionRepository.save(transaction);


            Candidate candidate = transaction.getCandidate();
            candidate.setPremium(true);


            Calendar c = Calendar.getInstance();
            c.setTime(new Date());
            c.add(Calendar.DATE, transaction.getSubscriptionPlan().getDurationInDays());
            candidate.setPremiumUntil(c.getTime());

            candidateRepository.save(candidate);

            return true;
        } else {

            transaction.setTransactionStatus("FAILED");
            transactionRepository.save(transaction);
            return false;
        }
    }
}