package com.example.ecommerce.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CartItem {
    private Integer goodsId;      // 商品ID
    private String goodsName;     // 商品名称
    private Integer price;        // 单价（分）
    private Integer quantity;     // 数量
    private Date addedAt;         // 添加时间
}
