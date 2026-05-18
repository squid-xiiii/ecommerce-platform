// AdminSystemLogController.java
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.SystemLog;
import com.example.ecommerce.repository.SystemLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/logs")
public class AdminSystemLogController {

    @Autowired
    private SystemLogRepository systemLogRepository;

    @GetMapping
    public ApiResponse<List<SystemLog>> getAllLogs(@RequestParam(defaultValue = "100") int limit) {
        List<SystemLog> logs = systemLogRepository.findAll();
        if (logs.size() > limit) {
            logs = logs.stream().limit(limit).toList();
        }
        return ApiResponse.success(logs);
    }

    @GetMapping("/errors")
    public ApiResponse<List<SystemLog>> getErrorLogs() {
        List<SystemLog> errors = systemLogRepository.findByLevel("ERROR");
        return ApiResponse.success(errors);
    }
}