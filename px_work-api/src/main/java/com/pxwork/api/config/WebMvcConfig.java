package com.pxwork.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * <p>
 * WebMvc 配置类
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${app.upload-dir:D:/px/backend/uploads}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:" + uploadDir + File.separator;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
