// AdminStatsController.java
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.admin.repository.CompletedOrderRepository;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class AdminStatsController {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ClickLogRepository clickLogRepository;

    @Autowired
    private CompletedOrderRepository completedOrderRepository;

    @GetMapping
    public ApiResponse<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGoods", goodsRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalComments", commentRepository.count());
        stats.put("totalClicks", clickLogRepository.count());
        stats.put("totalCompletedOrders", completedOrderRepository.count());

        // 分类商品统计
        long laptopCount = goodsRepository.findByCategory("笔记本电脑").size();
        long phoneCount = goodsRepository.findByCategory("手机").size();
        long accessoryCount = goodsRepository.findByCategory("配件").size();

        Map<String, Long> goodsByCategory = new HashMap<>();
        goodsByCategory.put("笔记本电脑", laptopCount);
        goodsByCategory.put("手机", phoneCount);
        goodsByCategory.put("配件", accessoryCount);
        stats.put("goodsByCategory", goodsByCategory);

        return ApiResponse.success(stats);
    }
}