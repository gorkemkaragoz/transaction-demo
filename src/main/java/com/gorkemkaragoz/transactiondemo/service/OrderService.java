package com.gorkemkaragoz.transactiondemo.service;

import com.gorkemkaragoz.transactiondemo.entity.Order;
import com.gorkemkaragoz.transactiondemo.exception.InsufficientStockException;

import java.util.List;

public interface OrderService {

    // DEMO 1 — Temel @Transactional: sipariş + stok aynı TX'de
    Order placeOrder(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // DEMO 2 — rollbackFor: checked exception ile rollback
    Order placeOrderWithCheckedEx(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // DEMO 3 — Dirty Checking: save() olmadan entity güncelleme
    void confirmOrder(Long orderId);

    // DEMO 4 — readOnly: dirty checking kapalı, sadece okuma
    List<Order> getAllOrders();

    // DEMO 5 — timeout: süre aşılınca rollback
    Order placeOrderWithTimeout(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // DEMO 6 — REQUIRES_NEW: log kaydı her zaman ayrı TX'de
    Order placeOrderWithAuditLog(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // DEMO 7 — Self-Invocation: proxy tuzağı, @Transactional etkisiz
    void selfInvocationDemo(Long orderId);

    // REQUIRED payment — sipariş rollback olursa payment da rollback olur
    Order placeOrderWithPayment(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // REQUIRES_NEW payment — sipariş rollback olsa bile payment veritabanında kalır
    Order placeOrderWithPaymentNewTx(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException;

    // DEMO 10 — cancelOrder: sipariş iptal + stok geri yükle
    void cancelOrder(Long orderId);
}