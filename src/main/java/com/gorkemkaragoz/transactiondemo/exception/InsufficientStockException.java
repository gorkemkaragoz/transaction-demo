package com.gorkemkaragoz.transactiondemo.exception;

public class InsufficientStockException extends Exception {

    public InsufficientStockException(String productName, int requested, int available) {
        super("Insufficient stock for product '" + productName + "'. Requested: " + requested + ", Available: " + available);
    }
}