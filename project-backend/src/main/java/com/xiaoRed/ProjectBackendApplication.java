package com.xiaoRed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.xiaoRed")
public class ProjectBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProjectBackendApplication.class, args);
    }

}
