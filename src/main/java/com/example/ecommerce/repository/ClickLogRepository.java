package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ClickLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ClickLogRepository extends MongoRepository<ClickLog, String> {
    long countByPageCode(String pageCode);
    List<ClickLog> findByPageCodeOrderByClickTimeDesc(String pageCode);
}