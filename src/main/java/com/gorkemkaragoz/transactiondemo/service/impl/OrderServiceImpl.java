package com.gorkemkaragoz.transactiondemo.service.impl;

import com.gorkemkaragoz.transactiondemo.entity.Order;
import com.gorkemkaragoz.transactiondemo.exception.InsufficientStockException;
import com.gorkemkaragoz.transactiondemo.repository.OrderRepository;
import com.gorkemkaragoz.transactiondemo.service.AuditLogService;
import com.gorkemkaragoz.transactiondemo.service.InventoryService;
import com.gorkemkaragoz.transactiondemo.service.OrderService;
import com.gorkemkaragoz.transactiondemo.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final AuditLogService auditLogService;

    // DEMO 1 — Temel @Transactional
    // Sipariş + stok aynı TX'de. Stok yetersizse her ikisi de rollback olur.
    @Override
    @Transactional(rollbackFor = InsufficientStockException.class)
    public Order placeOrder(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);
        log.info("Order saved: {}", order.getId());

        inventoryService.decreaseStock(productName, quantity);
        log.info("Stock decreased for order: {}", order.getId());

        return order;
    }

    // DEMO 2 — rollbackFor
    // InsufficientStockException checked exception'dır, normalde rollback tetiklemez.
    // rollbackFor olmadan sipariş orders tablosuna yazılır ama stok değişmez — veri tutarsızlığı oluşur.
    // rollbackFor = InsufficientStockException.class eklenince rollback olur.
    @Override
    @Transactional
    public Order placeOrderWithCheckedEx(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);
        log.info("Order saved: {}", order.getId());

        // decreaseStock() çağrılmıyor — exception direkt fırlatılıyor
        // Böylece rollbackFor olmadan checked exception'ın rollback tetiklemediği gösteriliyor
        throw new InsufficientStockException(productName, quantity, 0);
    }

    // DEMO 3 — Dirty Checking
    // save() yok. findById ile yüklenen entity managed state'e girer,
    // setStatus() sonrası Hibernate transaction commit'te otomatik UPDATE atar.
    @Override
    @Transactional
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        order.setStatus(Order.OrderStatus.CONFIRMED);
        log.info("Order confirmed (no save() called): {}", orderId);
    }

    // DEMO 4 — readOnly
    // Hibernate dirty checking'i kapatır, flush çalışmaz. Performans kazancı sağlar.
    @Override
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        log.info("Fetched {} orders (readOnly=true)", orders.size());
        return orders;
    }

    // DEMO 5 — timeout
    // 2 saniyelik timeout, 3 saniye bekleniyor. Timeout aşılınca transaction rollback olur.
    @Override
    @Transactional(timeout = 2, rollbackFor = InsufficientStockException.class)
    public Order placeOrderWithTimeout(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);
        log.info("Order saved, simulating delay...");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        inventoryService.decreaseStock(productName, quantity);

        return order;
    }

    // DEMO 6 — Propagation REQUIRES_NEW (AuditLog)
    // auditLogService.log() her zaman ayrı TX'de çalışır.
    // Ana TX rollback olsa bile log kaydı veritabanında kalır.
    @Override
    @Transactional(rollbackFor = InsufficientStockException.class)
    public Order placeOrderWithAuditLog(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        auditLogService.log("ORDER_ATTEMPT", "Customer: " + customerName + ", Product: " + productName);

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);

        inventoryService.decreaseStock(productName, quantity);

        auditLogService.log("ORDER_SUCCESS", "Order created with id: " + order.getId());

        return order;
    }

    // DEMO 7 — Self-Invocation Tuzağı
    // this.confirmOrder() proxy'yi atlar, @Transactional çalışmaz.
    // Düzeltmek için confirmOrder() ayrı bir @Service'e taşınmalıdır.
    @Override
    public void selfInvocationDemo(Long orderId) {
        log.warn("Self-invocation demo: calling this.confirmOrder() — @Transactional will NOT work");
        this.confirmOrder(orderId);
    }

    // DEMO 8 — Propagation REQUIRED
    // processPayment() REQUIRED ile çalışır, ana TX'e katılır.
    // Stok yetersizse hem sipariş hem payment rollback olur.
    @Override
    @Transactional(rollbackFor = InsufficientStockException.class)
    public Order placeOrderWithPayment(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);
        log.info("Order saved: {}", order.getId());

        paymentService.processPayment(order.getId(), totalPrice);
        log.info("Payment processed (REQUIRED) for order: {}", order.getId());

        inventoryService.decreaseStock(productName, quantity);
        log.info("Stock decreased for order: {}", order.getId());

        return order;
    }

    // DEMO 9 — Propagation REQUIRES_NEW
    // processPaymentInNewTransaction() her zaman yeni TX açar.
    // Stok yetersizse sipariş rollback olur ama payment veritabanında kalır.
    @Override
    @Transactional(rollbackFor = InsufficientStockException.class)
    public Order placeOrderWithPaymentNewTx(String customerName, String productName, int quantity, Double totalPrice) throws InsufficientStockException {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setProductName(productName);
        order.setQuantity(quantity);
        order.setTotalPrice(totalPrice);
        order.setStatus(Order.OrderStatus.PENDING);

        orderRepository.save(order);
        log.info("Order saved: {}", order.getId());

        paymentService.processPaymentInNewTransaction(order.getId(), totalPrice);
        log.info("Payment processed (REQUIRES_NEW) for order: {}", order.getId());

        inventoryService.decreaseStock(productName, quantity);
        log.info("Stock decreased for order: {}", order.getId());

        return order;
    }

    // DEMO 10 — cancelOrder
    // Siparişi CANCELLED yapar ve stoku geri yükler.
    // save() yok — dirty checking çalışır.
    // increaseStock() aynı TX'de çalışır, hata olursa her ikisi de rollback olur.
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled: " + orderId);
        }

        String productName = order.getProductName();
        int quantity = order.getQuantity();

        order.setStatus(Order.OrderStatus.CANCELLED);
        log.info("Order cancelled (no save() called): {}", orderId);

        inventoryService.increaseStock(productName, quantity);
        log.info("Stock restored for cancelled order: {}", orderId);
    }
}