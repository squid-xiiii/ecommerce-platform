package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.SystemLog;
import com.example.ecommerce.repository.SystemLogRepository;
import com.example.ecommerce.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class SystemLogServiceImpl extends SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public SystemLog saveLog(SystemLog log) {
        if (log.getCreatedTime() == null) {
            log.setCreatedTime(new Date());
        }
        return systemLogRepository.save(log);
    }

    @Override
    public List<SystemLog> findAllLogs() {
        return systemLogRepository.findAll();
    }

    @Override
    public List<SystemLog> findByLevel(String level) {
        return systemLogRepository.findByLevel(level);
    }
}