package com.xiaoRed.entity.postings;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class Post {
    private Integer id;                 // 帖子id
    private String title;               // 帖子标题
    private String content;             // 帖子内容
    private Integer userId;             // 用户id
    private String authorName;          // 作者名
    private String status;              // 帖子状态
    private String imageUrls;           // 图片地址
    private LocalDateTime createTime;   // 创建时间
    private LocalDateTime updateTime;   // 更新时间
    private List<String> images;        // 图片列表
}
