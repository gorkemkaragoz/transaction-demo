package com.gorkemkaragoz.transactiondemo.controller;

import com.gorkemkaragoz.transactiondemo.entity.Order;
import com.gorkemkaragoz.transactiondemo.exception.InsufficientStockException;
import com.gorkemkaragoz.transactiondemo.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // 1. Temel @Transactional
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrder(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // 2. rollbackFor
    @PostMapping("/place-checked")
    public ResponseEntity<Order> placeChecked(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrderWithCheckedEx(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // 3. Dirty Checking
    @PutMapping("/{orderId}/confirm")
    public ResponseEntity<String> confirmOrder(@PathVariable Long orderId) {
        orderService.confirmOrder(orderId);
        return ResponseEntity.ok("Order confirmed: " + orderId);
    }

    // 4. readOnly
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    // 5. timeout
    @PostMapping("/place-timeout")
    public ResponseEntity<Order> placeOrderWithTimeout(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrderWithTimeout(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // 6. REQUIRES_NEW — AuditLog
    @PostMapping("/place-with-audit")
    public ResponseEntity<Order> placeOrderWithAuditLog(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrderWithAuditLog(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // 7. Self-Invocation
    @PostMapping("/self-invocation")
    public ResponseEntity<String> selfInvocationDemo(@RequestParam Long orderId) {
        orderService.selfInvocationDemo(orderId);
        return ResponseEntity.ok("Self-invocation demo executed");
    }

    // DEMO 8 — Propagation REQUIRED
    @PostMapping("/place-with-payment")
    public ResponseEntity<Order> placeOrderWithPayment(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrderWithPayment(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // DEMO 9 — Propagation REQUIRES_NEW
    @PostMapping("/place-with-payment-new-tx")
    public ResponseEntity<Order> placeOrderWithPaymentNewTx(
            @RequestParam String customerName,
            @RequestParam String productName,
            @RequestParam int quantity,
            @RequestParam Double totalPrice) throws InsufficientStockException {
        Order order = orderService.placeOrderWithPaymentNewTx(customerName, productName, quantity, totalPrice);
        return ResponseEntity.ok(order);
    }

    // DEMO 10 — cancelOrder
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok("Order cancelled: " + orderId);
    }

}