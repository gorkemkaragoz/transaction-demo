package com.gorkemkaragoz.transactiondemo.service;

public interface AuditLogService {
    // Ana TX'den bağımsız, REQUIRES_NEW ile audit kaydı atar.
    void log(String action, String details);
}