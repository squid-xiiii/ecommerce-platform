package com.example.ecommerce.admin.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "completed_order")
public class CompletedOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long orderId;
    private String userName;
    private Integer totalAmount;
    private String status;
    private Date createdDate;
    private Date completedDate;
    @Column(columnDefinition = "TEXT")
    private String goodsList;
    private Date syncedAt;
}