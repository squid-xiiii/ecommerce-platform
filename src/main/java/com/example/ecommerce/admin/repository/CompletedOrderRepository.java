package com.example.ecommerce.admin.repository;

import com.example.ecommerce.admin.entity.CompletedOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompletedOrderRepository extends JpaRepository<CompletedOrder, Long> {
    boolean existsByOrderId(Long orderId);

}