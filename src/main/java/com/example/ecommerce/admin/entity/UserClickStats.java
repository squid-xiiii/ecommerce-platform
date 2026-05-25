// UserClickStats.java - 确保有这些字段
package com.example.ecommerce.admin.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

@Data
@Document(collection = "user_click_stats")
public class UserClickStats {
    @Id
    private String id;
    private String userId;
    private Integer totalClicks;
    private Date lastClickTime;
    private List<ClickHistoryItem> clickHistory;
    private List<Integer> recommendedGoods;
    private List<AdRecord> adsSent;  // 确保有这个字段
}

@Data
class ClickHistoryItem {
    private Integer goodsId;
    private String goodsName;
    private Integer count;
    private Date lastClick;
}

