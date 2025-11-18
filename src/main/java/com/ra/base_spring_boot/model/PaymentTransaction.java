package com.ra.base_spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "candidate_id")
    private Candidate candidate;
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "account_company_id")
    private AccountCompany accountCompany;
    @ManyToOne
    @JoinColumn(name = "plan_id")
    private SubscriptionPlan subscriptionPlan;

    private BigDecimal amount;

    private String bankCode;

    private String transactionStatus;

    private String vnpayOrderInfo;
    private String vnpayTxnRef;

    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentDate;


}