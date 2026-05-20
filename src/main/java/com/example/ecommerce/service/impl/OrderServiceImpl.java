package com.example.ecommerce.service.impl;

import java.util.Date;
import java.util.List;

import com.example.ecommerce.entity.Goods;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.ecommerce.admin.service.OrderSyncService;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.service.OrderService;
import com.example.ecommerce.service.SystemLogService;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderSyncService orderSyncService;

    @Autowired
    private SystemLogService systemLogService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private org.springframework.data.mongodb.core.MongoTemplate mongoTemplate;

    @Override
    public Order createOrder(Order order) {
        systemLogService.info("创建订单: userId=" + order.getUserName() + ", orderId=" + order.getOrderId());
        if (order.getCreatedDate() == null) {
            order.setCreatedDate(new Date());
        }
        // 如果状态为空，默认为"PAID"
        if (order.getStatus() == null) {
            order.setStatus("PAID");
        }
        // 如果订单完成，设置完成时间
        if ("COMPLETED".equals(order.getStatus()) && order.getCompletedDate() == null) {
            order.setCompletedDate(new Date());
        }

        // 原子性扣减库存（防并发）
        if (order.getGoodsList() != null) {
            for (OrderItem item : order.getGoodsList()) {
                // 仅当 stock >= quantity 时才会匹配并减库存
                org.springframework.data.mongodb.core.query.Query q =
                        new org.springframework.data.mongodb.core.query.Query(
                                org.springframework.data.mongodb.core.query.Criteria.where("goodsId").is(item.getGoodsId())
                                        .and("stock").gte(item.getQuantity())
                        );
                org.springframework.data.mongodb.core.query.Update u =
                        new org.springframework.data.mongodb.core.query.Update().inc("stock", -item.getQuantity());

                Goods updated = mongoTemplate.findAndModify(q, u,
                        org.springframework.data.mongodb.core.FindAndModifyOptions.options().returnNew(true), Goods.class);

                if (updated == null) {
                    // 如果不存在则区分是商品不存在还是库存不足
                    Goods existing = goodsService.findByGoodsId(item.getGoodsId())
                            .orElseThrow(() -> new RuntimeException("商品不存在: id=" + item.getGoodsId()));
                    throw new RuntimeException("库存不足: " + existing.getGoodsInfo());
                }
            }
        }

        return orderRepository.save(order);
    }

    @Override
    public List<Order> findByUserName(String userName) {
        return orderRepository.findByUserName(userName);
    }

    @Override
    public List<Order> findByUserNameAndStatus(String userName, String status) {
        return orderRepository.findByUserNameAndStatus(userName, status);
    }

    @Override
    public Order updateOrderStatus(String id, String status) {
        systemLogService.info("更新订单状态: orderId=" + id + ", newStatus=" + status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        if ("COMPLETED".equals(status)) {
            order.setCompletedDate(new Date());
            orderSyncService.syncSingleOrder(order);
        }
        return orderRepository.save(order);
    }

    @Override
    public void deleteOrder(String id) {
        orderRepository.deleteById(id);
    }

}