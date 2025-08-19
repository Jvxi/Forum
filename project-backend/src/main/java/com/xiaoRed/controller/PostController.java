package com.xiaoRed.controller;


import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manage")
@CrossOrigin("*")
public class PostController {
    @Resource
    PostService postService;
    
//    获取帖子列表
    @GetMapping("/lists")
    public List<Post> getPostList() {
        return postService.getAllPosts();
    }

//      修改帖子状态
    @PostMapping("/{postId}/status")
    public boolean updatePostStatus(@PathVariable int postId, @RequestBody String status) {
        return postService.updatePostStatus(postId, status);
    }
    
//    删除帖子
    @DeleteMapping("/post/{postId}")
    public boolean deletePost(@PathVariable int postId) {
        return postService.deletePostByAdmin(postId);
    }
}
