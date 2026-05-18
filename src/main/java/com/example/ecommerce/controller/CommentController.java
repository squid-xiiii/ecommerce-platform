package com.example.ecommerce.controller;

import com.example.ecommerce.common.ApiResponse;
import com.example.ecommerce.entity.Comment;
import com.example.ecommerce.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 发表评论
    @PostMapping
    public ResponseEntity<ApiResponse<Comment>> addComment(@RequestBody Comment comment) {
        Comment saved = commentService.saveComment(comment);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("评论发表成功", saved));
    }

    // 根据商品编号查询评论
    @GetMapping("/goods/{goodsId}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByGoodsId(@PathVariable Integer goodsId) {
        List<Comment> comments = commentService.findByGoodsIdOrderByTimeDesc(goodsId);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    // 新增：根据用户名查询评论（用于"我的评论"页面）
    @GetMapping("/user/{userName}")
    public ResponseEntity<ApiResponse<List<Comment>>> getCommentsByUserName(@PathVariable String userName) {
        List<Comment> comments = commentService.findByUserNameOrderByTimeDesc(userName);
        return ResponseEntity.ok(ApiResponse.success(comments));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(@PathVariable String id) {
        commentService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功", null));
    }
}