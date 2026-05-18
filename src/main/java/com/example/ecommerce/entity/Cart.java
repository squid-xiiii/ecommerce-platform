package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "carts")
@CompoundIndex(name = "user_cart_idx", def = "{'userId': 1}")
public class Cart {
    @Id
    private String id;
    private String userId;        // 用户ID或用户名
    private List<CartItem> items; // 购物车商品列表
    private Date updatedAt;       // 最后更新时间
}

