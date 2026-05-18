package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.service.GoodsService;
import com.example.ecommerce.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SystemLogService systemLogService;

    // 添加商品
    @PostMapping
    public ResponseEntity<ApiResponse<Goods>> createGoods(@RequestBody Goods goods) {
        Goods saved = goodsService.saveGoods(goods);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("商品添加成功", saved));
    }

    // 根据商品编号查询
    @GetMapping("/{goodsId}")
    public ResponseEntity<ApiResponse<Goods>> getGoodsByGoodsId(@PathVariable Integer goodsId) {
        return goodsService.findByGoodsId(goodsId)
                .map(goods -> ResponseEntity.ok(ApiResponse.success(goods)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "商品不存在")));
    }

    // 查询所有商品（可按分类筛选）
    @GetMapping
    public ResponseEntity<ApiResponse<List<Goods>>> getAllGoods(
            @RequestParam(required = false) String category) {
        List<Goods> goods;
        if (category != null && !category.isEmpty()) {
            goods = goodsService.findByCategory(category);
        } else {
            goods = goodsService.findAllGoods();
        }
        return ResponseEntity.ok(ApiResponse.success(goods));
    }

    // 更新商品
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Goods>> updateGoods(@PathVariable String id, @RequestBody Goods goods) {
        goods.setId(id); // 确保 ID 一致
        Goods updated = goodsService.updateGoods(goods);
        return ResponseEntity.ok(ApiResponse.success("更新成功", updated));
    }

    // 删除商品
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGoods(@PathVariable String id) {
        goodsService.deleteGoods(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}