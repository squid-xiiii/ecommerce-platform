package com.example.ecommerce.service;

import com.example.ecommerce.entity.Goods;
import java.util.List;
import java.util.Optional;

public interface GoodsService {
    Goods saveGoods(Goods goods);
    Optional<Goods> findByGoodsId(Integer goodsId);
    List<Goods> findAllGoods();
    List<Goods> findByCategory(String category);
    void deleteGoods(String id);
    Goods updateGoods(Goods goods);
}
