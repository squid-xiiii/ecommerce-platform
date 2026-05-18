package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.SystemLog;
import com.example.ecommerce.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class SystemLogController {

    @Autowired
    private SystemLogService systemLogService;

    // 添加系统日志
    @PostMapping
    public ResponseEntity<ApiResponse<SystemLog>> addLog(@RequestBody SystemLog log) {
        SystemLog saved = systemLogService.saveLog(log);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("日志已记录", saved));
    }

    // 查询所有系统日志（可按级别筛选）
    @GetMapping
    public ResponseEntity<ApiResponse<List<SystemLog>>> getAllLogs(
            @RequestParam(required = false) String level) {
        List<SystemLog> logs;
        if (level != null && !level.isEmpty()) {
            logs = systemLogService.findByLevel(level);
        } else {
            logs = systemLogService.findAllLogs();
        }
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}