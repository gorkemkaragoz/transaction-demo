package com.gorkemkaragoz.transactiondemo.service.impl;

import com.gorkemkaragoz.transactiondemo.entity.AuditLog;
import com.gorkemkaragoz.transactiondemo.repository.AuditLogRepository;
import com.gorkemkaragoz.transactiondemo.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    // REQUIRES_NEW — Ana TX'den bağımsız çalışır, her zaman yeni TX açar.
    // Ana TX rollback olsa bile bu kayıt veritabanında kalır.
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void log(String action, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setDetails(details);
        auditLog.setCreatedAt(LocalDateTime.now());

        auditLogRepository.save(auditLog);
        log.info("AuditLog saved: action={}, details={}", action, details);
    }

}