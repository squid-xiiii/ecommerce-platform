package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.ClickLog;
import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.repository.ClickLogRepository;
import com.example.ecommerce.service.ClickLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ecommerce.repository.GoodsRepository;

import java.util.*;

@Service
public class ClickLogServiceImpl implements ClickLogService {

    @Autowired
    private ClickLogRepository clickLogRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public ClickLog saveClickLog(ClickLog clickLog) {
        if (clickLog.getClickTime() == null) {
            clickLog.setClickTime(new Date());
        }
        return clickLogRepository.save(clickLog);
    }

    @Override
    public long countByPageCode(String pageCode) {
        return clickLogRepository.countByPageCode(pageCode);
    }

    @Override
    public List<ClickLog> getRecentClicksByPage(String pageCode, int limit) {
        List<ClickLog> all = clickLogRepository.findByPageCodeOrderByClickTimeDesc(pageCode);
        if (all.size() > limit) {
            return all.subList(0, limit);
        }
        return all;
    }

    @Override
    public List<ClickLog> getUserClickLogs(String userId) {
        return clickLogRepository.findByUserIdOrderByClickTimeDesc(userId);
    }

    @Override
    public Map<String, Object> getUserClickStats(String userId) {
        List<ClickLog> userClicks = clickLogRepository.findByUserIdOrderByClickTimeDesc(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("totalClicks", userClicks.size());
        stats.put("lastClickTime", userClicks.isEmpty() ? null : userClicks.get(0).getClickTime());

        // 按商品统计点击
        Map<Integer, Integer> goodsClickCount = new HashMap<>();
        for (ClickLog log : userClicks) {
            // 从 pageContent 或 clickPosition 中提取 goodsId
            Integer goodsId = extractGoodsIdFromLog(log);
            if (goodsId != null) {
                goodsClickCount.put(goodsId, goodsClickCount.getOrDefault(goodsId, 0) + 1);
            }
        }

        // 转换为列表
        List<Map<String, Object>> clickHistory = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : goodsClickCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("goodsId", entry.getKey());
            item.put("goodsName", getGoodsName(entry.getKey()));
            item.put("count", entry.getValue());
            clickHistory.add(item);
        }
        clickHistory.sort((a, b) -> (Integer)b.get("count") - (Integer)a.get("count"));
        stats.put("clickHistory", clickHistory);

        return stats;
    }

    @Override
    public List<Map<String, Object>> getGoodsClickRanking() {
        // 获取所有商品详情页的点击记录
        List<ClickLog> allGoodsClicks = clickLogRepository.findAll();

        Map<Integer, Integer> goodsClickCount = new HashMap<>();
        for (ClickLog log : allGoodsClicks) {
            Integer goodsId = extractGoodsIdFromLog(log);
            if (goodsId != null) {
                goodsClickCount.put(goodsId, goodsClickCount.getOrDefault(goodsId, 0) + 1);
            }
        }

        // 转换为列表并排序
        List<Map<String, Object>> ranking = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : goodsClickCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("goodsId", entry.getKey());
            item.put("goodsName", getGoodsName(entry.getKey()));
            item.put("clickCount", entry.getValue());
            ranking.add(item);
        }
        ranking.sort((a, b) -> (Integer)b.get("clickCount") - (Integer)a.get("clickCount"));

        return ranking;
    }

    @Override
    public List<Map<String, Object>> getUserClickRanking() {
        // 获取所有点击记录，按用户分组统计
        List<ClickLog> allClicks = clickLogRepository.findAll();

        Map<String, Map<String, Object>> userStats = new HashMap<>();
        for (ClickLog log : allClicks) {
            String userId = log.getUserId();
            if (userId == null || userId.equals("游客")) continue;

            Map<String, Object> stats = userStats.get(userId);
            if (stats == null) {
                stats = new HashMap<>();
                stats.put("userId", userId);
                stats.put("totalClicks", 0);
                stats.put("lastClickTime", log.getClickTime());
                userStats.put(userId, stats);
            }
            stats.put("totalClicks", (Integer)stats.get("totalClicks") + 1);

            Date lastTime = (Date)stats.get("lastClickTime");
            if (lastTime == null || log.getClickTime().after(lastTime)) {
                stats.put("lastClickTime", log.getClickTime());
            }
        }

        List<Map<String, Object>> ranking = new ArrayList<>(userStats.values());
        ranking.sort((a, b) -> (Integer)b.get("totalClicks") - (Integer)a.get("totalClicks"));

        return ranking;
    }

    @Override
    public List<Integer> getRecommendedGoodsForUser(String userId, int limit) {
        // 基于用户点击历史推荐商品
        List<ClickLog> userClicks = clickLogRepository.findByUserIdOrderByClickTimeDesc(userId);

        // 统计用户点击过的商品类别
        Map<String, Integer> categoryCount = new HashMap<>();
        Set<Integer> clickedGoodsIds = new HashSet<>();

        for (ClickLog log : userClicks) {
            Integer goodsId = extractGoodsIdFromLog(log);
            if (goodsId != null) {
                clickedGoodsIds.add(goodsId);
                Optional<Goods> goodsOpt = goodsRepository.findByGoodsId(goodsId);
                goodsOpt.ifPresent(goods -> {
                    String category = goods.getCategory();
                    categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
                });
            }
        }

        // 找出用户最感兴趣的类别
        String topCategory = null;
        int maxCount = 0;
        for (Map.Entry<String, Integer> entry : categoryCount.entrySet()) {
            if (entry.getValue() > maxCount) {
                maxCount = entry.getValue();
                topCategory = entry.getKey();
            }
        }

        // 推荐同类别中未被点击过的商品
        List<Integer> recommendations = new ArrayList<>();
        if (topCategory != null) {
            List<Goods> categoryGoods = goodsRepository.findByCategory(topCategory);
            for (Goods goods : categoryGoods) {
                if (!clickedGoodsIds.contains(goods.getGoodsId())) {
                    recommendations.add(goods.getGoodsId());
                    if (recommendations.size() >= limit) break;
                }
            }
        }

        // 如果推荐不足，补充热门商品
        if (recommendations.size() < limit) {
            List<Map<String, Object>> goodsRanking = getGoodsClickRanking();
            for (Map<String, Object> item : goodsRanking) {
                Integer goodsId = (Integer) item.get("goodsId");
                if (!clickedGoodsIds.contains(goodsId) && !recommendations.contains(goodsId)) {
                    recommendations.add(goodsId);
                    if (recommendations.size() >= limit) break;
                }
            }
        }

        return recommendations;
    }

    // ========== 辅助方法 ==========

    private Integer extractGoodsIdFromLog(ClickLog log) {
        // 从 pageContent 或 url 中提取商品ID
        String url = log.getUrl();
        if (url != null && url.contains("/user/goods/")) {
            try {
                return Integer.parseInt(url.substring(url.lastIndexOf("/") + 1));
            } catch (NumberFormatException e) {
                return null;
            }
        }

        String content = log.getPageContent();
        if (content != null && content.matches(".*\\d+.*")) {
            // 尝试从内容中提取数字作为商品ID
            String numStr = content.replaceAll("\\D", "");
            if (!numStr.isEmpty()) {
                try {
                    return Integer.parseInt(numStr);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }

    private String getGoodsName(Integer goodsId) {
        return goodsRepository.findByGoodsId(goodsId)
                .map(Goods::getGoodsInfo)
                .orElse("未知商品");
    }
}