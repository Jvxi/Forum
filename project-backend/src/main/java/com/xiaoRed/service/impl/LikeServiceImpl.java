package com.xiaoRed.service.impl;

import com.xiaoRed.entity.vindicate.Like;
import com.xiaoRed.mapper.LikeMapper;
import com.xiaoRed.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    LikeMapper likeMapper;
    
    @Override
    public List<Like> getPostList() {
        return likeMapper.selectList();
    }
}
