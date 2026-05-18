package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.ClickLog;
import com.example.ecommerce.service.ClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clicks")
public class ClickLogController {

    @Autowired
    private ClickLogService clickLogService;

    // 记录点击日志
    @PostMapping
    public ResponseEntity<ApiResponse<ClickLog>> logClick(@RequestBody ClickLog clickLog) {
        ClickLog saved = clickLogService.saveClickLog(clickLog);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("点击已记录", saved));
    }

    // 统计某个页面的点击总数
    @GetMapping("/count/{pageCode}")
    public ResponseEntity<ApiResponse<Long>> getClickCount(@PathVariable String pageCode) {
        long count = clickLogService.countByPageCode(pageCode);
        return ResponseEntity.ok(ApiResponse.success(count));
    }

    // 获取某个页面的最近 N 条点击记录
    @GetMapping("/recent/{pageCode}")
    public ResponseEntity<ApiResponse<?>> getRecentClicks(
            @PathVariable String pageCode,
            @RequestParam(defaultValue = "10") int limit) {
        var recent = clickLogService.getRecentClicksByPage(pageCode, limit);
        return ResponseEntity.ok(ApiResponse.success(recent));
    }
}