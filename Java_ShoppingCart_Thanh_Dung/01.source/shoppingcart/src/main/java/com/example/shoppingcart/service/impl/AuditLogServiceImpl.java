package com.example.shoppingcart.service.impl;

import com.example.shoppingcart.entity.AuditLog;
import com.example.shoppingcart.exception.NotFoundException;
import com.example.shoppingcart.repository.AuditLogRepository;
import com.example.shoppingcart.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AuditLogServiceImpl implements AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Override
    public List<AuditLog> getAllAuditLog() {
        List<AuditLog> auditLogs = auditLogRepository.findAll();
        if (!auditLogs.isEmpty()) {
            return auditLogRepository.findAll();
        }
        else {
            throw new NotFoundException("No audit log found!");
        }
    }
}
