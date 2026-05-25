package com.example.ecommerce.service;

import com.example.ecommerce.entity.ClickLog;
import java.util.List;
import java.util.Map;

public interface ClickLogService {
    ClickLog saveClickLog(ClickLog clickLog);
    long countByPageCode(String pageCode);
    List<ClickLog> getRecentClicksByPage(String pageCode, int limit);

    List<ClickLog> getUserClickLogs(String userId);

    // 获取用户的点击统计
    Map<String, Object> getUserClickStats(String userId);

    // 获取商品点击排行
    List<Map<String, Object>> getGoodsClickRanking();

    // 获取用户点击排行
    List<Map<String, Object>> getUserClickRanking();

    // 获取用户感兴趣的推荐商品ID（基于点击历史）
    List<Integer> getRecommendedGoodsForUser(String userId, int limit);
}