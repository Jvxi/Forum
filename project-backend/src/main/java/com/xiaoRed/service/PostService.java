package com.xiaoRed.service;


import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.entity.postings.PostVO;
import org.springframework.stereotype.Service;

import java.util.List;



public interface PostService {
//    帖子
    List<PostVO> getPostList();
    PostVO getPostById(Integer postId);
    void createPost(PostVO post);
    boolean updatePost(PostVO postVO, Integer userId);
    boolean deletePost(int postId, int userId);
//    标签
    List<PostVO> getAllTags();
//    管理
    List<Post> getAllPosts();
//    修改帖子状态
    boolean updatePostStatus(int postId, String status);
//    删除帖子
    boolean deletePostByAdmin(int postId);
}
