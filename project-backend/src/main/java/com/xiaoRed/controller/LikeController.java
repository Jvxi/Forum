package com.xiaoRed.controller;


import com.xiaoRed.entity.vindicate.Like;
import com.xiaoRed.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
