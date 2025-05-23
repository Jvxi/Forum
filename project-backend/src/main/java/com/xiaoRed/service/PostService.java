package com.xiaoRed.service;


import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.entity.postings.PostVO;
import org.springframework.stereotype.Service;

import java.util.List;



public interface PostService {
    List<PostVO> getPostList();
    PostVO getPostById(Integer postId);
    void createPost(PostVO post);
    boolean updatePost(PostVO postVO, Integer userId);
//    标签
    List<PostVO> getAllTags();
}
