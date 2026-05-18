package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 创建订单
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("订单创建成功", created));
    }

    // 查询用户的所有订单
    @GetMapping("/user/{userName}")
    public ResponseEntity<ApiResponse<List<Order>>> getUserOrders(@PathVariable String userName) {
        List<Order> orders = orderService.findByUserName(userName);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // 查询用户的历史订单（已完成）
    @GetMapping("/user/{userName}/history")
    public ResponseEntity<ApiResponse<List<Order>>> getUserHistoryOrders(@PathVariable String userName) {
        List<Order> orders = orderService.findByUserNameAndStatus(userName, "COMPLETED");
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    // 更新订单状态
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable String id, @RequestParam String status) {
        Order updated = orderService.updateOrderStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("状态更新成功", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}