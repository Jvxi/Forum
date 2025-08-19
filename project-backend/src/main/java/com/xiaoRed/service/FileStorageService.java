package com.xiaoRed.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String baseUploadDir;

    @Value("${file.avatar-base-url}")
    private String avatarBaseUrl;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        // 使用配置的基路径
        this.rootLocation = Paths.get(baseUploadDir);
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("无法创建上传目录: " + baseUploadDir, e);
        }
    }

    public String storeAvatar(MultipartFile file, long userId) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件为空");
        }

        // 1. 创建用户专属目录
        Path userDir = rootLocation.resolve("avatars").resolve(String.valueOf(userId));
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        // 2. 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + extension;

        // 3. 保存文件
        Path targetPath = userDir.resolve(filename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // 4. 返回访问URL
        return avatarBaseUrl + userId + "/" + filename;
    }

    /**
     * 删除旧头像文件
     */
    public void deleteOldAvatar(String oldAvatarUrl) {
        if (oldAvatarUrl == null || oldAvatarUrl.isEmpty()) return;

        try {
            // 从URL中提取相对路径
            String relativePath = oldAvatarUrl.replace(avatarBaseUrl, "");
            Path oldPath = rootLocation.resolve("avatars").resolve(relativePath);

            if (Files.exists(oldPath)) {
                Files.delete(oldPath);
            }
        } catch (IOException e) {
            // 记录错误但不要中断流程
            System.err.println("删除旧头像失败: " + e.getMessage());
        }
    }
}