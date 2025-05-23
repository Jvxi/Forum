package com.xiaoRed.entity.postings;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class Post {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;
    private String imageUrls; // 新增字段
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> images; // 新增图片列表
}
