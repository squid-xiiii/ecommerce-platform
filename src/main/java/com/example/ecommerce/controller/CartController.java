package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    // 获取购物车
    @GetMapping("/{userId}")
    public ApiResponse<Cart> getCart(@PathVariable String userId) {
        return ApiResponse.success(cartService.getCart(userId));
    }

    // 添加商品到购物车
    @PostMapping("/{userId}/add")
    public ApiResponse<Cart> addToCart(@PathVariable String userId, @RequestBody Map<String, Object> params) {
        Integer goodsId = (Integer) params.get("goodsId");
        Integer quantity = (Integer) params.get("quantity");
        return ApiResponse.success(cartService.addToCart(userId, goodsId, quantity));
    }

    // 更新购物车商品数量
    @PutMapping("/{userId}/update")
    public ApiResponse<Cart> updateCartItem(@PathVariable String userId, @RequestBody Map<String, Object> params) {
        Integer goodsId = (Integer) params.get("goodsId");
        Integer quantity = (Integer) params.get("quantity");
        return ApiResponse.success(cartService.updateCartItem(userId, goodsId, quantity));
    }

    // 删除购物车商品
    @DeleteMapping("/{userId}/remove/{goodsId}")
    public ApiResponse<Cart> removeCartItem(@PathVariable String userId, @PathVariable Integer goodsId) {
        return ApiResponse.success(cartService.removeCartItem(userId, goodsId));
    }

    // 清空购物车
    @DeleteMapping("/{userId}/clear")
    public ApiResponse<Cart> clearCart(@PathVariable String userId) {
        return ApiResponse.success(cartService.clearCart(userId));
    }

    // 同步购物车
    @PostMapping("/{userId}/sync")
    public ApiResponse<Cart> syncCart(@PathVariable String userId, @RequestBody List<CartItem> items) {
        return ApiResponse.success(cartService.syncCart(userId, items));
    }
}