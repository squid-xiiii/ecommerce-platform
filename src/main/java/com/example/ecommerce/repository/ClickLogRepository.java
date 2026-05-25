package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ClickLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClickLogRepository extends MongoRepository<ClickLog, String> {
    long countByPageCode(String pageCode);
    List<ClickLog> findByPageCodeOrderByClickTimeDesc(String pageCode);

    List<ClickLog> findByUserIdOrderByClickTimeDesc(String userId);

    // 2. 统计某个用户的点击总数
    long countByUserId(String userId);

    // 3. 按商品统计点击量（从 pageContent 中提取商品信息）
    @Query("{ 'pageCode': 'goods_detail', 'userId': ?0 }")
    List<ClickLog> findUserGoodsClicks(String userId);

    // 4. 统计每个商品的点击量（所有用户）
    @Query("{ 'pageCode': 'goods_detail' }")
    List<ClickLog> findAllGoodsDetailClicks();

    // 5. 查询最近活跃用户（按最后点击时间排序）
    @Query(value = "{ }", sort = "{ 'clickTime': -1 }")
    List<ClickLog> findRecentClicks();
}