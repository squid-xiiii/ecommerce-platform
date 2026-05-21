package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "comments")
public class Comment {
    @Id
    private String id;
    private String userName;
    private Integer goodsId;
    private String goodsName;// 商品编号
    private String content;
    private Integer star;         // 1-5
    private List<String> commentLabels; // 标签数组，不再存JSON字符串
    private Date createdTime;
}