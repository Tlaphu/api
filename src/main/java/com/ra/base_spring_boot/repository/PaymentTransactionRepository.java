package com.ra.base_spring_boot.repository;

import com.ra.base_spring_boot.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {


    Optional<PaymentTransaction> findByVnpayTxnRef(String vnpayTxnRef);
}