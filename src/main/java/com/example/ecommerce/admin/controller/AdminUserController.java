// AdminUserController.java - 直接使用 ClickLogService
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.ClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.ecommerce.admin.entity.AdRecord;
import com.example.ecommerce.admin.entity.UserClickStats;
import org.springframework.web.bind.annotation.*;
import com.example.ecommerce.admin.repository.UserClickStatsRepository;

import java.util.*;

@RestController
@RequestMapping("/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClickLogService clickLogService;

    @Autowired
    private UserClickStatsRepository clickStatsRepository;// 复用现有的服务

    // 获取所有用户
    @GetMapping
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword(null));
        return ApiResponse.success(users);
    }

    // 获取用户的点击统计
    @GetMapping("/{userId}/clicks")
    public ApiResponse<Map<String, Object>> getUserClickStats(@PathVariable String userId) {
        return ApiResponse.success(clickLogService.getUserClickStats(userId));
    }

    // 获取商品点击排行
    @GetMapping("/goods-ranking")
    public ApiResponse<List<Map<String, Object>>> getGoodsClickRanking() {
        return ApiResponse.success(clickLogService.getGoodsClickRanking());
    }

    // 获取用户点击排行
    @GetMapping("/user-ranking")
    public ApiResponse<List<Map<String, Object>>> getUserClickRanking() {
        return ApiResponse.success(clickLogService.getUserClickRanking());
    }

    // 获取用户推荐商品
    @GetMapping("/{userId}/recommend")
    public ApiResponse<List<Integer>> getRecommendations(@PathVariable String userId,
                                                         @RequestParam(defaultValue = "6") int limit) {
        return ApiResponse.success(clickLogService.getRecommendedGoodsForUser(userId, limit));
    }
    // 发送广告接口
    @PostMapping("/{userId}/send-ad")
    public ApiResponse<Void> sendAd(@PathVariable String userId, @RequestBody Map<String, String> adInfo) {
        // 获取或创建用户点击统计
        UserClickStats stats = clickStatsRepository.findByUserId(userId)
                .orElse(new UserClickStats());

        stats.setUserId(userId);
        if (stats.getAdsSent() == null) {
            stats.setAdsSent(new ArrayList<>());
        }

        AdRecord ad = new AdRecord();
        ad.setAdId(UUID.randomUUID().toString());
        ad.setContent(adInfo.get("content"));
        ad.setSentTime(new Date());
        ad.setStatus("sent");
        stats.getAdsSent().add(ad);

        clickStatsRepository.save(stats);

        System.out.println("向用户 " + userId + " 发送广告: " + adInfo.get("content"));

        return ApiResponse.success("广告发送成功", null);
    }

    // 获取用户的消息通知
    @GetMapping("/{userId}/messages")
    public ApiResponse<List<AdRecord>> getUserMessages(@PathVariable String userId) {
        UserClickStats stats = clickStatsRepository.findByUserId(userId)
                .orElse(new UserClickStats());
        return ApiResponse.success(stats.getAdsSent() != null ? stats.getAdsSent() : new ArrayList<>());
    }

    // 标记消息为已读
    @PutMapping("/{userId}/messages/{adId}/read")
    public ApiResponse<Void> markMessageAsRead(@PathVariable String userId, @PathVariable String adId) {
        UserClickStats stats = clickStatsRepository.findByUserId(userId)
                .orElse(new UserClickStats());
        if (stats.getAdsSent() != null) {
            stats.getAdsSent().stream()
                    .filter(ad -> ad.getAdId().equals(adId))
                    .findFirst()
                    .ifPresent(ad -> ad.setStatus("read"));
            clickStatsRepository.save(stats);
        }
        return ApiResponse.success("标记成功", null);
    }
}