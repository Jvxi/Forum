package com.xiaoRed.service;

import com.xiaoRed.entity.postings.Comment;

import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByPostId(Integer postId);
    void createComment(Comment comment, Integer userId);
    void deleteComment(Integer commentId, Integer userId);
}
