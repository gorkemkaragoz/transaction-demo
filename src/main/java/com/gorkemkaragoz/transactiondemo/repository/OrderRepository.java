package com.gorkemkaragoz.transactiondemo.repository;

import com.gorkemkaragoz.transactiondemo.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}