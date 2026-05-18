// AdminGoodsController.java
package com.example.ecommerce.admin.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.service.GoodsService;
import com.example.ecommerce.service.SystemLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/goods")
public class AdminGoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private SystemLogService systemLogService;

    @GetMapping
    public ApiResponse<List<Goods>> getAllGoods() {
        return ApiResponse.success(goodsService.findAllGoods());
    }

    @GetMapping("/{goodsId}")
    public ApiResponse<Goods> getGoods(@PathVariable Integer goodsId) {
        return goodsService.findByGoodsId(goodsId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "商品不存在"));
    }

    @PostMapping
    public ApiResponse<Goods> createGoods(@RequestBody Goods goods) {
        systemLogService.info("添加商品: goodsId=" + goods.getGoodsId() + ", name=" + goods.getGoodsInfo());
        return ApiResponse.success(goodsService.saveGoods(goods));
    }

    @PutMapping("/{id}")
    public ApiResponse<Goods> updateGoods(@PathVariable String id, @RequestBody Goods goods) {
        systemLogService.info("更新商品: id=" + id);
        goods.setId(id);
        return ApiResponse.success(goodsService.updateGoods(goods));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteGoods(@PathVariable String id) {
        systemLogService.info("删除商品: id=" + id);
        goodsService.deleteGoods(id);
        return ApiResponse.success("删除成功", null);
    }
}