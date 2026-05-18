// AdminOrderController.java
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.admin.entity.CompletedOrder;
import com.example.ecommerce.admin.repository.CompletedOrderRepository;
import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CompletedOrderRepository completedOrderRepository;

    @GetMapping
    public ApiResponse<List<Order>> getAllOrders() {
        return ApiResponse.success(orderRepository.findAll());
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<Order> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        return ApiResponse.success(orderService.updateOrderStatus(id, status));
    }

    @GetMapping("/completed")
    public ApiResponse<List<CompletedOrder>> getCompletedOrders() {
        return ApiResponse.success(completedOrderRepository.findAll());
    }
}