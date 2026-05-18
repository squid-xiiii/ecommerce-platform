package com.example.ecommerce.service;

import com.example.ecommerce.entity.Order;
import java.util.List;

public interface OrderService {
    Order createOrder(Order order);
    List<Order> findByUserName(String userName);
    List<Order> findByUserNameAndStatus(String userName, String status);
    Order updateOrderStatus(String id, String status);

    void deleteOrder(String id);
}