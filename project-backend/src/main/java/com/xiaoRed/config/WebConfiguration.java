package com.xiaoRed.config;

import com.xiaoRed.interceptor.AuthorizeInterceptor;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    @Resource
    AuthorizeInterceptor authorizeInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                .addInterceptor(authorizeInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/auth/**")
                .excludePathPatterns("/api/likes/**")
                .excludePathPatterns("/api/manage/**")
                .excludePathPatterns("/api/comments/**")
                .excludePathPatterns("/api/posts/**")
                .excludePathPatterns("/api/user/upload-avatar")
                .excludePathPatterns("/uploads/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String base = uploadDir.endsWith("/") ? uploadDir : uploadDir + "/";
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations("file:" + base + "avatars/")
                .setCachePeriod(3600); // 1小时缓存
    }
}
