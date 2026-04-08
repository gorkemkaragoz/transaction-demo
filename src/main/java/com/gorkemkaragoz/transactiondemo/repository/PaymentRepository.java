package com.gorkemkaragoz.transactiondemo.repository;

import com.gorkemkaragoz.transactiondemo.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}