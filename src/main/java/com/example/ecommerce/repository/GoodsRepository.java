package com.example.ecommerce.repository;
import com.example.ecommerce.entity.Goods;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
public interface GoodsRepository extends MongoRepository<Goods, String> {
    Optional<Goods> findByGoodsId(Integer goodsId);
    List<Goods> findByCategory(String category);
}
