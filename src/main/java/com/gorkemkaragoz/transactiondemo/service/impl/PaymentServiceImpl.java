package com.gorkemkaragoz.transactiondemo.service.impl;

import com.gorkemkaragoz.transactiondemo.entity.Payment;
import com.gorkemkaragoz.transactiondemo.repository.PaymentRepository;
import com.gorkemkaragoz.transactiondemo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // REQUIRED — Çağıran TX varsa ona katılır, yoksa yeni açar.
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processPayment(Long orderId, Double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        paymentRepository.save(payment);
        log.info("Payment processed (REQUIRED): orderId={}, amount={}", orderId, amount);
    }

    // REQUIRES_NEW — Her zaman yeni TX açar, çağıran TX'den bağımsız çalışır.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processPaymentInNewTransaction(Long orderId, Double amount) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus(Payment.PaymentStatus.COMPLETED);

        paymentRepository.save(payment);
        log.info("Payment processed (REQUIRES_NEW): orderId={}, amount={}", orderId, amount);
    }

}