package com.example.recruiting_system.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // New uploads folder
        Path uploadDir = Paths.get("uploads");
        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");

        // Legacy resumes folder (for backward compatibility)
        Path resumeDir = Paths.get("resumes");
        String resumePath = resumeDir.toFile().getAbsolutePath();

        registry.addResourceHandler("/resumes/**")
                .addResourceLocations("file:" + resumePath + "/");
    }
}
