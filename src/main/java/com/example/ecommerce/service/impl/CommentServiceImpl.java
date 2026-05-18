package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.Comment;
import com.example.ecommerce.repository.CommentRepository;
import com.example.ecommerce.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment saveComment(Comment comment) {
        if (comment.getCreatedTime() == null) {
            comment.setCreatedTime(new Date());
        }
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByGoodsId(Integer goodsId) {
        return commentRepository.findByGoodsId(goodsId);
    }

    @Override
    public List<Comment> findByGoodsIdOrderByTimeDesc(Integer goodsId) {
        return commentRepository.findByGoodsIdOrderByCreatedTimeDesc(goodsId);
    }

    @Override
    public List<Comment> findByUserName(String userName) {
        return commentRepository.findByUserName(userName);
    }

    @Override
    public List<Comment> findByUserNameOrderByTimeDesc(String userName) {
        return commentRepository.findByUserNameOrderByCreatedTimeDesc(userName);
    }

    @Override
    public void deleteComment(String id) {
        commentRepository.deleteById(id);
    }
}