package com.pxwork.api.controller.frontend;

import cn.dev33.satoken.stp.StpUtil;
import com.pxwork.common.entity.User;
import com.pxwork.common.request.FrontendLoginRequest;
import com.pxwork.common.service.UserService;
import com.pxwork.common.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result<Map<String, Object>> login(@RequestBody @Validated FrontendLoginRequest loginRequest) {
        String token = userService.login(loginRequest);
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("tokenName", StpUtil.getTokenName());
        tokenInfo.put("tokenValue", token);
        return Result.success(tokenInfo);
    }

    @Operation(summary = "学员注销登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("注销成功");
    }

    @Operation(summary = "获取当前学员信息")
    @GetMapping("/user/info")
    public Result<User> userInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }
}
