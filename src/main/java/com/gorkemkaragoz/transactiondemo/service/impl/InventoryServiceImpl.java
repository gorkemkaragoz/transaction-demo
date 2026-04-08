package com.gorkemkaragoz.transactiondemo.service.impl;

import com.gorkemkaragoz.transactiondemo.entity.Inventory;
import com.gorkemkaragoz.transactiondemo.exception.InsufficientStockException;
import com.gorkemkaragoz.transactiondemo.repository.InventoryRepository;
import com.gorkemkaragoz.transactiondemo.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    // sipariş gelince o ürünün stokunu düşürür.
    @Override
    @Transactional
    public void decreaseStock(String productName, int quantity) throws InsufficientStockException {
        Inventory inventory = inventoryRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productName));

        if (inventory.getQuantity() < quantity) {
            throw new InsufficientStockException(productName, quantity,  inventory.getQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        log.info("Stock decreased: product={}, quantity={}", productName, quantity);

    }

    // sipariş iptal edilince stoku geri yükler.
    @Override
    @Transactional
    public void increaseStock(String productName, int quantity) {
        Inventory inventory = inventoryRepository.findByProductName(productName)
                .orElseThrow(() -> new RuntimeException("Product not found: " + productName));

        inventory.setQuantity(inventory.getQuantity() + quantity);
        log.info("Stock increased: product={}, quantity={}", productName, quantity);

    }

}
