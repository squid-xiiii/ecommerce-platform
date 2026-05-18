package com.example.ecommerce.service;

import com.example.ecommerce.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment saveComment(Comment comment);
    List<Comment> findByGoodsId(Integer goodsId);
    List<Comment> findByGoodsIdOrderByTimeDesc(Integer goodsId);
    List<Comment> findByUserName(String userName);
    List<Comment> findByUserNameOrderByTimeDesc(String userName);

    void deleteComment(String id);
}