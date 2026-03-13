package com.pxwork.api.controller.backend;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.utils.Result;
import com.pxwork.system.entity.AdminUser;
import com.pxwork.system.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "后台登录管理")
@RestController
@RequestMapping("/backend")
public class BackendLoginController {

    @Autowired
    private AdminUserService adminUserService;

    @Operation(summary = "后台管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 1. 根据邮箱查询用户
        AdminUser adminUser = adminUserService.getOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getEmail, loginRequest.getEmail()));

        if (adminUser == null) {
            return Result.fail("账号或密码错误");
        }

        // 2. 校验密码
        String inputPassword = SaSecureUtil.sha256(loginRequest.getPassword());
        if (!inputPassword.equals(adminUser.getPassword())) {
            return Result.fail("账号或密码错误");
        }

        // 3. 登录
        StpUtil.login(adminUser.getId());

        // 4. 返回 Token 信息
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("tokenName", StpUtil.getTokenName());
        tokenInfo.put("tokenValue", StpUtil.getTokenValue());
        tokenInfo.put("userId", adminUser.getId());
        tokenInfo.put("userName", adminUser.getName());

        return Result.success(tokenInfo);
    }

    @Operation(summary = "后台注销登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("注销成功");
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
