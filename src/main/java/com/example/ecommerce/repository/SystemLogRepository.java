package com.example.ecommerce.repository;

import com.example.ecommerce.entity.SystemLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SystemLogRepository extends MongoRepository<SystemLog, String> {
    List<SystemLog> findByLevel(String level);
}