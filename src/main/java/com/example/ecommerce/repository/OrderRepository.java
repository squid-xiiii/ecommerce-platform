package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    // 根据用户名查询所有订单（包括当前和历史）
    List<Order> findByUserName(String userName);

    // 根据用户名和订单状态查询（例如查询历史订单 status="COMPLETED"）
    List<Order> findByUserNameAndStatus(String userName, String status);

    List<Order> findByStatus(String completed);
}