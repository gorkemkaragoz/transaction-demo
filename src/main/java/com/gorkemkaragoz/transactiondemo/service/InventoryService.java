package com.gorkemkaragoz.transactiondemo.service;

import com.gorkemkaragoz.transactiondemo.exception.InsufficientStockException;

public interface InventoryService {

    // Stok düşürür. Yetersiz stokta InsufficientStockException fırlatır.
    void decreaseStock(String productName, int quantity) throws InsufficientStockException;

    // Stok artırır. Sipariş iptali senaryosunda kullanılır.
    void increaseStock(String productName, int quantity);

}