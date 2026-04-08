package com.gorkemkaragoz.transactiondemo.repository;

import com.gorkemkaragoz.transactiondemo.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}