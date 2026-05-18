package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    @Indexed(unique = true)
    private Long orderId;
    private String userName;
    private List<OrderItem> goodsList;
    private Integer totalAmount;
    private String status;   // CART, PAID, SHIPPED, COMPLETED
    private Date createdDate;
    private Date completedDate; // 完成时间（历史订单）
}