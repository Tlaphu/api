package com.ra.base_spring_boot.controller;

import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.dto.req.FormPayment;
import com.ra.base_spring_boot.dto.resp.PaymentResponse;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import com.ra.base_spring_boot.repository.PaymentTransactionRepository;
import com.ra.base_spring_boot.repository.SubscriptionPlanRepository;
import com.ra.base_spring_boot.services.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;
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
    private final IAccountCompanyRepository accountCompanyRepository;


    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(
            @RequestBody FormPayment request,
            HttpServletRequest httpServletRequest) {

        String vnpayTxnRef = UUID.randomUUID().toString();
        Candidate candidate = null;
        AccountCompany companyAccount = null;

        try {

            if ("CANDIDATE".equalsIgnoreCase(request.getAccountType())) {
                candidate = candidateRepository.findById(request.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Candidate not found"));
            } else if ("COMPANY".equalsIgnoreCase(request.getAccountType())) {
                companyAccount = accountCompanyRepository.findById(request.getAccountId())
                        .orElseThrow(() -> new RuntimeException("Company Account not found"));
            } else {
                throw new RuntimeException("Invalid account type: Must be CANDIDATE or COMPANY.");
            }

            SubscriptionPlan plan = planRepository.findById(request.getPlanId())
                    .orElseThrow(() -> new RuntimeException("Plan not found"));


            String orderInfo = String.format("Nâng cấp VIP cho %s ID: %d - Plan: %s",
                    request.getAccountType(), request.getAccountId(), plan.getName());


            PaymentTransaction newTransaction = PaymentTransaction.builder()
                    .candidate(candidate)
                    .accountCompany(companyAccount)
                    .subscriptionPlan(plan)
                    .amount(plan.getPrice())
                    .transactionStatus("PENDING")
                    .vnpayTxnRef(vnpayTxnRef)
                    .vnpayOrderInfo(orderInfo)
                    .build();
            transactionRepository.save(newTransaction);


            String paymentUrl = vnpayService.createPaymentUrl(
                    httpServletRequest,
                    plan.getPrice(),
                    orderInfo,
                    vnpayTxnRef
            );

            PaymentResponse response = PaymentResponse.builder()
                    .paymentUrl(paymentUrl)
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


    @GetMapping("/transactions")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<PaymentTransaction>> getAllTransactions() {

        List<PaymentTransaction> transactions = transactionRepository.findAll();

        return ResponseEntity.ok(transactions);
    }
}