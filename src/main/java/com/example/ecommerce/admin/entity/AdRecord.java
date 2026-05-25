package com.example.ecommerce.admin.entity;

import lombok.Data;

import java.util.Date;

@Data
public class AdRecord {
    private String adId;
    private String content;
    private Date sentTime;
    private String status;  // sent, read, clicked
}
