package com.example.ecommerce.admin.repository;

import com.example.ecommerce.admin.entity.UserClickStats;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserClickStatsRepository extends MongoRepository<UserClickStats, String> {
    Optional<UserClickStats> findByUserId(String userId);
}