package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "goods")
public class Goods {
    @Id
    private String id;
    @Indexed(unique = true)
    private Integer goodsId;       // 商品编号
    private String goodsInfo;      // 商品基本信息
    private String specificationsInfo; // 规格信息
    private String adInfo;         // 广告信息
    private Integer price;         // 单价（分）
    private Integer stock;         // 库存
    private String category;
}