package com.xiaoRed.controller;


import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.vindicate.Like;
import com.xiaoRed.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin("*")
public class LikeController {
    @Resource
    LikeService likeService;

    @GetMapping("/list")
    public ResponseEntity<List<Like>> getPostList() {
        List<Like> posts = likeService.getPostList();
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createPost(@RequestBody Like post) {
        likeService.createPost(post);
        return ResponseEntity.ok().build();
    }
}
