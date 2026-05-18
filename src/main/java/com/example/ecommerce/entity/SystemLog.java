package com.example.ecommerce.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@Document(collection = "system_logs")
public class SystemLog {
    @Id
    private String id;
    private String level;       // INFO, WARN, ERROR
    private String message;
    private String stackTrace;
    private Date createdTime;
}