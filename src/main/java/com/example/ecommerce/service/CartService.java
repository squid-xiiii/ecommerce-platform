package com.example.ecommerce.service;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.GoodsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private GoodsRepository goodsRepository;

    // 获取用户购物车
    public Cart getCart(String userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setItems(new ArrayList<>());
            newCart.setUpdatedAt(new Date());
            return newCart;
        });
    }

    // 添加商品到购物车
    public Cart addToCart(String userId, Integer goodsId, Integer quantity) {
        Cart cart = getCart(userId);
        List<CartItem> items = cart.getItems();

        Optional<CartItem> existingItem = items.stream()
                .filter(item -> item.getGoodsId().equals(goodsId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            Optional<Goods> goodsOpt = goodsRepository.findByGoodsId(goodsId);
            if (goodsOpt.isPresent()) {
                Goods goods = goodsOpt.get();
                CartItem newItem = new CartItem();
                newItem.setGoodsId(goodsId);
                newItem.setGoodsName(goods.getGoodsInfo());
                newItem.setPrice(goods.getPrice());
                newItem.setQuantity(quantity);
                newItem.setAddedAt(new Date());
                items.add(newItem);
            }
        }

        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    // 更新购物车商品数量
    public Cart updateCartItem(String userId, Integer goodsId, Integer quantity) {
        Cart cart = getCart(userId);
        List<CartItem> items = cart.getItems();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getGoodsId().equals(goodsId)) {
                if (quantity <= 0) {
                    items.remove(i);
                } else {
                    items.get(i).setQuantity(quantity);
                }
                break;
            }
        }

        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    // 删除购物车商品
    public Cart removeCartItem(String userId, Integer goodsId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getGoodsId().equals(goodsId));
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    // 清空购物车
    public Cart clearCart(String userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }

    // 同步本地购物车到服务器（合并）
    public Cart syncCart(String userId, List<CartItem> localItems) {
        Cart cart = getCart(userId);
        List<CartItem> serverItems = cart.getItems();

        for (CartItem localItem : localItems) {
            Optional<CartItem> existing = serverItems.stream()
                    .filter(item -> item.getGoodsId().equals(localItem.getGoodsId()))
                    .findFirst();

            if (existing.isPresent()) {
                existing.get().setQuantity(localItem.getQuantity());
            } else {
                serverItems.add(localItem);
            }
        }

        cart.setUpdatedAt(new Date());
        return cartRepository.save(cart);
    }
}