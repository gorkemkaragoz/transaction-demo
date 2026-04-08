package com.gorkemkaragoz.transactiondemo.service;

public interface PaymentService {

    // REQUIRED propagation ile ödeme kaydı oluşturur.
    void processPayment(Long orderId, Double amount);

    // REQUIRES_NEW propagation ile ödeme kaydı oluşturur.
    void processPaymentInNewTransaction(Long orderId, Double amount);
}