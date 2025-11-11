package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.Candidate;
import com.ra.base_spring_boot.model.PaymentTransaction;
import com.ra.base_spring_boot.model.SubscriptionPlan;
import com.ra.base_spring_boot.dto.req.FormPayment; // SỬ DỤNG CLASS REQUEST MỚI
import com.ra.base_spring_boot.dto.resp.PaymentResponse; // SỬ DỤNG CLASS RESPONSE MỚI
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.PaymentTransactionRepository;
import com.ra.base_spring_boot.repository.SubscriptionPlanRepository; // Cần tạo Repository này
import com.ra.base_spring_boot.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final VNPayService vnpayService;
    private final ICandidateRepository candidateRepository;
    private final SubscriptionPlanRepository planRepository;
    private final PaymentTransactionRepository transactionRepository;


    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody FormPayment request,
            HttpServletRequest httpServletRequest) {

        String vnpayTxnRef = UUID.randomUUID().toString();

        try {
            Candidate candidate = candidateRepository.findById(request.getCandidateId())
                    .orElseThrow(() -> new RuntimeException("Candidate not found"));
            SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));


            PaymentTransaction newTransaction = PaymentTransaction.builder()
                    .candidate(candidate)
                    .subscriptionPlan(plan)
                    .amount(plan.getPrice())
                    .transactionStatus("PENDING")
                    .vnpayTxnRef(vnpayTxnRef)
                    .vnpayOrderInfo("Nâng cấp VIP cho Candidate ID: " + candidate.getId() + " - Plan: " + plan.getName())
                    .build();
            transactionRepository.save(newTransaction);


            String paymentUrl = vnpayService.createPaymentUrl(
                    httpServletRequest,
                    plan.getPrice(),
                    newTransaction.getVnpayOrderInfo(),
                    vnpayTxnRef
            );


            PaymentResponse response = PaymentResponse.builder()
                    .paymentUrl(paymentUrl) // URL VNPay để redirect
                    .vnpayTxnRef(vnpayTxnRef)
                    .message("Payment URL created successfully. Redirect user to this URL.")
                    .build();

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            PaymentResponse errorResponse = PaymentResponse.builder()
                    .message("Error creating payment: " + e.getMessage())
                    .vnpayTxnRef(vnpayTxnRef)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/vnpay_return")
    public RedirectView handleVNPayReturn(@RequestParam Map<String, String> params) {


        boolean isSuccess = vnpayService.handleVNPayReturn(params);

        if (isSuccess) {

            return new RedirectView("http://localhost:5173/payment/success?status=00");
        } else {

            return new RedirectView("http://localhost:5173/payment/failure?status=" + params.getOrDefault("vnp_ResponseCode", "99"));
        }
    }
}