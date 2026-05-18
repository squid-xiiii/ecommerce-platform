package com.example.ecommerce.service;

import com.example.ecommerce.entity.SystemLog;
import com.example.ecommerce.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public abstract class SystemLogService {

    @Autowired
    private SystemLogRepository systemLogRepository;

    public void info(String message) {
        saveLog("INFO", message, null);
    }

    public void warn(String message) {
        saveLog("WARN", message, null);
    }

    public void error(String message, Exception e) {
        String stackTrace = e != null ? getStackTrace(e) : null;
        saveLog("ERROR", message, stackTrace);
    }

    public void error(String message) {
        saveLog("ERROR", message, null);
    }

    private void saveLog(String level, String message, String stackTrace) {
        SystemLog log = new SystemLog();
        log.setLevel(level);
        log.setMessage(message);
        log.setStackTrace(stackTrace);
        log.setCreatedTime(new Date());
        systemLogRepository.save(log);
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
            if (sb.length() > 1000) break;
        }
        return sb.toString();
    }

    public abstract SystemLog saveLog(SystemLog log);

    public abstract List<SystemLog> findAllLogs();

    public abstract List<SystemLog> findByLevel(String level);
}