package com.xiaoRed.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Account是数据库直接映射的实体类
 * 而这个AccountUser是存放用户详细信息的，根据不同项目的需求可以进行扩充
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountUser {
    long id;
    String username;
    String email;
    String avatar; // 新增头像URL字段
}
