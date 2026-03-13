package com.pxwork.api.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 后台管理拦截器
 * 负责校验后台管理员是否登录
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 校验后台管理员是否登录 (如果未登录，会抛出 NotLoginException)
        StpUtil.checkLogin();
        return true;
    }
}
