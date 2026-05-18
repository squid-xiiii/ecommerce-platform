package com.example.ecommerce.entity;

import lombok.Data;

@Data
public class OrderItem {
    private Integer goodsId;
    private String goodsName;
    private Integer quantity;
    private Integer price;   // 购买时单价
}