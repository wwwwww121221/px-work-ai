package com.pxwork.api.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {

    @Bean
    public SaInterceptor saInterceptor() {
        return new SaInterceptor(handle -> SaRouter
                .match("/**")
                .notMatch(
                        "/",
                        "/error",
                        "/doc.html",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**"
                )
                .check(r -> {
                    String method = cn.dev33.satoken.context.SaHolder.getRequest().getMethod();
                    if (!"GET".equalsIgnoreCase(method)) {
                        StpUtil.checkLogin();
                    }
                }));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(saInterceptor()).addPathPatterns("/**");
    }
}
