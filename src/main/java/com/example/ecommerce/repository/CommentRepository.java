package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByGoodsId(Integer goodsId);
    List<Comment> findByGoodsIdOrderByCreatedTimeDesc(Integer goodsId);
    List<Comment> findByUserName(String userName);
    // 新增：根据用户名查询评论，按时间倒序
    List<Comment> findByUserNameOrderByCreatedTimeDesc(String userName);
}