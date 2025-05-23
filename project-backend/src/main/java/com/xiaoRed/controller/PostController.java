package com.xiaoRed.controller;

import com.xiaoRed.entity.postings.Post;
import com.xiaoRed.entity.postings.PostVO;
import com.xiaoRed.service.PostService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin("*")
public class PostController {
    @Resource
    PostService postService;

    @GetMapping("/list")
    public ResponseEntity<List<PostVO>> getPostList() {
        List<PostVO> posts = postService.getPostList();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostVO> getPostDetail(@PathVariable int postId) {
        try {
            PostVO post = postService.getPostById(postId);
            return ResponseEntity.ok(post);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE) // 显式声明消费JSON
    public ResponseEntity<?> createPost(@RequestBody PostVO post, HttpServletRequest request) {
        Integer userId = post.getUserId();      //从前端获取userId，如果前端没传会自动为null
        Integer tagId = post.getTagId();
        post.setUserId(userId);
        post.setTagId(tagId);
        postService.createPost(post);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable int postId,
            @RequestBody PostVO postVO) {

        try {
            // 从请求属性获取当前用户ID
            Integer userId = postVO.getUserId();      //从前端获取userId，如果前端没传会自动为null
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户未认证");
            }

            // 设置需要更新的postID
            postVO.setId(postId);

            // 调用Service层进行更新
            boolean success = postService.updatePost(postVO, userId);

            if (success) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("无操作权限");
            }
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("帖子不存在");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误");
        }
    }

//    获取所有标签
    @GetMapping("/tags")
    public ResponseEntity<List<PostVO>> getAllTags() {
        return ResponseEntity.ok(postService.getAllTags());
    }
}
