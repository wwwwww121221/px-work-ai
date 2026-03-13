package com.pxwork.api.controller.frontend;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pxwork.common.entity.User;
import com.pxwork.common.service.UserService;
import com.pxwork.common.utils.Result;
import com.pxwork.common.utils.StpUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "前台学员登录")
@RestController
@RequestMapping("/frontend")
public class FrontendLoginController {

    @Autowired
    private UserService userService;

    @Operation(summary = "学员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest loginRequest) {
        // 1. 根据邮箱查询学员
        User user = userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, loginRequest.getEmail()));

        if (user == null) {
            return Result.fail("账号或密码错误");
        }

        // 2. 校验密码
        String inputPassword = SaSecureUtil.sha256(loginRequest.getPassword());
        if (!inputPassword.equals(user.getPassword())) {
            return Result.fail("账号或密码错误");
        }

        // 3. 登录 (使用学员专用 StpUserUtil)
        StpUserUtil.login(user.getId());

        // 4. 返回 Token 信息
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("tokenName", StpUserUtil.getTokenValue()); // TokenName 通常是配置的Key，这里直接返回Value方便前端
        tokenInfo.put("token", StpUserUtil.getTokenValue());
        tokenInfo.put("userId", user.getId());
        tokenInfo.put("userName", user.getName());

        return Result.success(tokenInfo);
    }

    @Operation(summary = "学员注销登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUserUtil.logout();
        return Result.success("注销成功");
    }

    @Operation(summary = "获取当前学员信息")
    @GetMapping("/user/detail")
    public Result<User> getUserDetail() {
        long userId = StpUserUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null); //不仅要把密码设为空，安全起见
        }
        return Result.success(user);
    }

    @Data
    public static class LoginRequest {
        private String email;
        private String password;
    }
}
