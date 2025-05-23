package com.xiaoRed.service.impl;

import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.entity.postings.PostVO;
import com.xiaoRed.mapper.PostMapper;
import com.xiaoRed.mapper.UserMapper;
import com.xiaoRed.service.PostService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostServiceImpl implements PostService {
    @Resource
    PostMapper postMapper;


    @Override
    public List<PostVO> getPostList() {
        // 直接返回Mapper查询结果（已包含作者信息）
        return postMapper.selectPostList();
    }

    @Override
    public PostVO getPostById(Integer postId) {
        return postMapper.selectPostById(postId);
    }

    @Override
    public void createPost(PostVO post) {
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        postMapper.insertPost(post);
    }

    @Override
    @Transactional
    public boolean updatePost(PostVO postVO, Integer userId) {
        // 1. 验证帖子存在性
        postVO.setUserId(userId);
        if (postVO == null) {
            throw new NoSuchElementException("帖子不存在");
        }

        // 2. 验证操作权限
        if (!postVO.getUserId().equals(userId)) {
            return false;
        }

        // 3. 更新字段
        postVO.setId(postVO.getId());
        postVO.setTitle(postVO.getTitle());
        postVO.setContent(postVO.getContent());
        postVO.setUpdateTime(LocalDateTime.now());

        // 4. 执行更新
        return postMapper.updatePost(postVO) > 0;
    }

//    标签
    @Override
    public List<PostVO> getAllTags() {
        return postMapper.selectAllTags();
    }
}
