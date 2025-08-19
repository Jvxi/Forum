package com.xiaoRed.service;


import com.xiaoRed.entity.postings.PostVO;
import com.xiaoRed.entity.vindicate.Like;

import java.util.List;


public interface LikeService {
    List<Like> getPostList();
    void createPost(Like post);
}
