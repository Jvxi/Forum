package com.xiaoRed.service.impl;

import com.xiaoRed.entity.postings.Comment;
import com.xiaoRed.mapper.CommentMapper;
import com.xiaoRed.service.CommentService;
import jakarta.annotation.Resource;
import org.springframework.data.relational.core.sql.In;
import org.springframework.data.relational.core.sql.Insert;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    CommentMapper commentMapper;
    
//    创建评论
    @Override
    @Transactional
    public void createComment(Comment comment, Integer userId) {
        comment.setUserId(userId);
        comment.setCreateTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        commentMapper.insert(comment);
        commentMapper.selectById(comment.getId()); // 可选：查询完整信息
    }

//  查询帖子下的所有评论
    @Override
    public List<Comment> getCommentsByPostId(Integer postId) {
        return commentMapper.selectByPostId(postId);
    }
    
//    删除评论
    @Override
    @Transactional
    public void deleteComment(Integer commentId, Integer userId) {
        int affected = commentMapper.delete(commentId, userId);
        if (affected == 0) {
            throw new NoSuchElementException("删除失败：评论不存在或无权操作");
        }
    }
}
