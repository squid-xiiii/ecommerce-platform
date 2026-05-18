// AdminClickLogController.java
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.ClickLog;
import com.example.ecommerce.repository.ClickLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/clicks")
public class AdminClickLogController {

    @Autowired
    private ClickLogRepository clickLogRepository;

    @GetMapping
    public ApiResponse<List<ClickLog>> getAllClicks(@RequestParam(defaultValue = "100") int limit) {
        List<ClickLog> clicks = clickLogRepository.findAll();
        if (clicks.size() > limit) {
            clicks = clicks.stream().limit(limit).collect(Collectors.toList());
        }
        return ApiResponse.success(clicks);
    }

    @GetMapping("/stats")
    public ApiResponse<Object> getClickStats() {
        List<ClickLog> allClicks = clickLogRepository.findAll();
        long totalCount = allClicks.size();

        // 按页面统计点击量
        var pageStats = allClicks.stream()
                .collect(Collectors.groupingBy(ClickLog::getPageCode, Collectors.counting()));

        return ApiResponse.success(Map.of(
                "totalCount", totalCount,
                "pageStats", pageStats
        ));
    }
}