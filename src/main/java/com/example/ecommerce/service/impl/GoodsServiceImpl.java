package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.repository.GoodsRepository;
import com.example.ecommerce.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Override
    public Goods saveGoods(Goods goods) {
        return goodsRepository.save(goods);
    }

    @Override
    public Optional<Goods> findByGoodsId(Integer goodsId) {
        return goodsRepository.findByGoodsId(goodsId);
    }

    @Override
    public List<Goods> findAllGoods() {
        return goodsRepository.findAll();
    }

    @Override
    public List<Goods> findByCategory(String category) {
        return goodsRepository.findByCategory(category);
    }

    @Override
    public void deleteGoods(String id) {
        goodsRepository.deleteById(id);
    }

    @Override
    public Goods updateGoods(Goods goods) {
        // 确保 goods 存在（可根据需要增加存在性检查）
        return goodsRepository.save(goods);
    }

    @Override
    public boolean decreaseStock(Integer goodsId, Integer quantity) {
        Optional<Goods> goodsOpt = goodsRepository.findByGoodsId(goodsId);
        if (goodsOpt.isPresent()) {
            Goods goods = goodsOpt.get();
            if (goods.getStock() >= quantity) {
                goods.setStock(goods.getStock() - quantity);
                goodsRepository.save(goods);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean increaseStock(Integer goodsId, Integer quantity) {
        Optional<Goods> goodsOpt = goodsRepository.findByGoodsId(goodsId);
        if (goodsOpt.isPresent()) {
            Goods goods = goodsOpt.get();
            goods.setStock(goods.getStock() + quantity);
            goodsRepository.save(goods);
            return true;
        }
        return false;
    }
}