package com.xiaoRed.entity.postings;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostVO {
    private Integer id;
    private String title;
    private String content;
    private Integer userId;
    private String authorName;  // 对应SQL中的u.username
    private String avatar;      // 作者头像
    private String status;              // 帖子状态
    private Integer tagId;
    private String tagName;          //标签
    private String images; // 新增字段
    private List<String> imageUrls; // 新增图片列表
    private LocalDateTime createTime;  // 映射数据库的create_time字段
    private LocalDateTime updateTime;
}