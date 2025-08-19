package com.xiaoRed.controller;


import com.xiaoRed.entity.postings.Comment;
import com.xiaoRed.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin("*")
public class CommentController {
    @Resource
    CommentService commentService;

    @PostMapping("/create")
//    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createComment(
            @RequestBody Comment comment) {
        try {
            Integer userId = comment.getUserId();
            if (userId == null || userId <= 0) {
                throw new NoSuchElementException("无效的用户ID");
            }
            commentService.createComment(comment, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"error\": \"服务器错误: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<Comment>> getCommentsByPost(@PathVariable Integer postId) {
        List<Comment> comment = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(
            @PathVariable Integer id,
            @RequestBody Comment comment) {
        Integer userId = comment.getUserId();
        commentService.deleteComment(id, userId);
        return ResponseEntity.ok().build();
    }
}
