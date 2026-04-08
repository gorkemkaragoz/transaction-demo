package com.gorkemkaragoz.transactiondemo.repository;

import com.gorkemkaragoz.transactiondemo.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductName(String productName);
}