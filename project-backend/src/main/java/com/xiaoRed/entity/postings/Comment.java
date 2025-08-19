package com.xiaoRed.entity.postings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.xiaoRed.entity.auth.Account;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer id;
    private Integer postId;
    private Integer userId;
    private String content;
    private String authorName;
    private String avatar;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
