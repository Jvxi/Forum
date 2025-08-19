package com.xiaoRed.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xiaoRed.entity.RestBean;
import com.xiaoRed.entity.user.AccountInfo;
import com.xiaoRed.entity.user.AccountPrivacy;
import com.xiaoRed.entity.user.AccountUser;
import com.xiaoRed.service.FileStorageService;
import com.xiaoRed.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;


@Slf4j
@Validated
@RestController
@RequestMapping("/api/user")
public class UserController {
    //邮件地址的正则表达式
    private final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,}$";
    @Resource
    UserService userService;
    @Resource
    FileStorageService fileStorageService;


    /**
     * prehandle()方法将登录成功的用户详细信息存放在请求体的session中
     * 调用这个接口时，从请求体中拿到用户详细信息
     * 然后直接将它返回给前端，前端就可以很方便地操纵这些信息了
     */
    @GetMapping("/me")
    public RestBean<AccountUser> me(@SessionAttribute("accountUser") AccountUser accountUser){
        return RestBean.success(accountUser);
    }

    @PostMapping("/save-info")
    public RestBean<String> saveInfo(@RequestBody @Validated AccountInfo accountInfo,
                                   @SessionAttribute("accountUser") AccountUser accountUser){
        accountInfo.setUid(accountUser.getId());//将uid给到用户信息
        if(userService.saveUserInfo(accountInfo)){
            accountUser.setUsername(accountInfo.getUsername());//将Session中存的用户名也要改掉
            accountUser.setAvatar(accountInfo.getAvatar()); // 更新session中的头像
            return RestBean.success();
        }else{
            return RestBean.failure(400,"用户名已存在，无法修改");
        }
    }

    @GetMapping("/info")
    public RestBean<AccountInfo> info(@SessionAttribute("accountUser") AccountUser accountUser){
        return RestBean.success(userService.userInfo(accountUser.getId()));
    }

    @PostMapping("/save-email")
    public RestBean<String> saveEmail(@Pattern (regexp = EMAIL_REGEX)@RequestParam("email") String email,
                                      @SessionAttribute("accountUser") AccountUser accountUser){
        if(userService.saveEmail(email,accountUser.getId())){
            accountUser.setEmail(email);//session中的也要进行相应修改
            return RestBean.success();
        }else{
            return RestBean.failure(400,"邮件地址已被其他用户使用，无法修改");
        }
    }

    @PostMapping("/change-password")
    public RestBean<String> changePassword(@Length(min = 6, max =16) @RequestParam("old") String old_paw,
                                           @Length(min = 6, max =16) @RequestParam("new") String new_paw,
                                           @SessionAttribute("accountUser") AccountUser accountUser){
        if(userService.changePassword(old_paw, new_paw, accountUser.getId())){
            return RestBean.success();
        }else{
            return RestBean.failure(400, "原密码错误");
        }
    }

    @PostMapping("/save-privacy")
    public RestBean<String> savePrivacy(@RequestBody AccountPrivacy accountPrivacy,
                                        @SessionAttribute("accountUser") AccountUser accountUser){
        accountPrivacy.setUid(accountUser.getId());
        userService.saveUserPrivacy(accountPrivacy);
        return RestBean.success();
    }

    @GetMapping("/privacy")
    public RestBean<AccountPrivacy> privacy(@SessionAttribute("accountUser") AccountUser accountUser){
        return RestBean.success(userService.userPrivacy(accountUser.getId()));
    }

    // 头像上传接口
    @PostMapping(
            value = "/upload-avatar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userInfo") String userInfoJson) { // 接收前端传递的用户信息JSON

        try {
            // 1. 解析前端传递的用户信息
            ObjectMapper objectMapper = new ObjectMapper();
            AccountUser accountUser = objectMapper.readValue(userInfoJson, AccountUser.class);

            log.info("收到用户 {} 的头像上传请求", accountUser.getUsername());

            // 2. 验证用户信息（可选，但推荐）
            if (!userService.isValidUser(accountUser.getId(), accountUser.getUsername())) {
                log.warn("用户信息验证失败: {}", accountUser);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(RestBean.failure(401, "用户信息无效"));
            }

            // 3. 删除旧头像
            if (accountUser.getAvatar() != null && !accountUser.getAvatar().isEmpty()) {
                log.debug("删除旧头像: {}", accountUser.getAvatar());
                fileStorageService.deleteOldAvatar(accountUser.getAvatar());
            }

            // 4. 存储新头像
            String avatarUrl = fileStorageService.storeAvatar(file, accountUser.getId());
            log.info("头像存储成功: {}", avatarUrl);

            // 5. 更新用户头像（数据库）
            userService.updateAvatar(accountUser.getId(), avatarUrl);

            // 6. 返回成功响应（包含新头像URL）
            return ResponseEntity.ok(RestBean.success(avatarUrl));
        } catch (Exception e) {
            log.error("头像上传失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RestBean.failure(500, "头像上传失败: " + e.getMessage()));
        }
    }
}
