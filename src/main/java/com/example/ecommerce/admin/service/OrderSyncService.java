// OrderSyncService.java
package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.entity.CompletedOrder;
import com.example.ecommerce.admin.repository.CompletedOrderRepository;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class OrderSyncService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CompletedOrderRepository completedOrderRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 定时同步：每小时执行一次
    @Scheduled(cron = "0 0 * * * ?")
    public void syncCompletedOrders() {
        List<Order> completedOrders = orderRepository.findByStatus("COMPLETED");

        for (Order order : completedOrders) {
            if (!completedOrderRepository.existsByOrderId(order.getOrderId())) {
                CompletedOrder completedOrder = new CompletedOrder();
                completedOrder.setOrderId(order.getOrderId());
                completedOrder.setUserName(order.getUserName());
                completedOrder.setTotalAmount(order.getTotalAmount());
                completedOrder.setStatus(order.getStatus());
                completedOrder.setCreatedDate(order.getCreatedDate());
                completedOrder.setCompletedDate(order.getCompletedDate());
                completedOrder.setSyncedAt(new Date());

                try {
                    String goodsJson = objectMapper.writeValueAsString(order.getGoodsList());
                    completedOrder.setGoodsList(goodsJson);
                } catch (Exception e) {
                    completedOrder.setGoodsList("[]");
                }

                completedOrderRepository.save(completedOrder);
                System.out.println("同步订单: " + order.getOrderId());
            }
        }
    }

    // 实时同步单个订单（当订单状态变为 COMPLETED 时调用）
    public void syncSingleOrder(Order order) {
        if ("COMPLETED".equals(order.getStatus()) && !completedOrderRepository.existsByOrderId(order.getOrderId())) {
            CompletedOrder completedOrder = new CompletedOrder();
            completedOrder.setOrderId(order.getOrderId());
            completedOrder.setUserName(order.getUserName());
            completedOrder.setTotalAmount(order.getTotalAmount());
            completedOrder.setStatus(order.getStatus());
            completedOrder.setCreatedDate(order.getCreatedDate());
            completedOrder.setCompletedDate(order.getCompletedDate());
            completedOrder.setSyncedAt(new Date());

            try {
                String goodsJson = objectMapper.writeValueAsString(order.getGoodsList());
                completedOrder.setGoodsList(goodsJson);
            } catch (Exception e) {
                completedOrder.setGoodsList("[]");
            }

            completedOrderRepository.save(completedOrder);
        }
    }
}