package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "clicks_logs")
public class ClickLog {
    @Id
    private String id;
    private String pageCode;
    private String url;
    private String clickPosition;
    private String pageContent;
    private String userId;      // 可为空（未登录用户）
    private Date clickTime;
}