package com.pxwork.api.config;

import com.pxwork.api.interceptor.AdminInterceptor;
import com.pxwork.api.interceptor.FrontInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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

    @Bean
    public AdminInterceptor adminInterceptor() {
        return new AdminInterceptor();
    }

    @Bean
    public FrontInterceptor frontInterceptor() {
        return new FrontInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册后台管理拦截器
        registry.addInterceptor(adminInterceptor())
                .addPathPatterns("/backend/**")
                .excludePathPatterns("/backend/login", "/backend/captchaImage"); // 排除登录和验证码接口

        // 注册前台学员拦截器
        registry.addInterceptor(frontInterceptor())
                .addPathPatterns("/frontend/**")
                .excludePathPatterns(
                        "/frontend/login",
                        "/frontend/register",
                        "/frontend/captchaImage",
                        "/frontend/course/list", // 假设公开课程列表不需要登录
                        "/frontend/course/detail/**" // 假设公开课程详情不需要登录
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = "file:" + uploadDir + File.separator;

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}
