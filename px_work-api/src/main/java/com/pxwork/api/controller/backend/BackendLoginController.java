package com.pxwork.api.controller.backend;

import cn.dev33.satoken.stp.StpUtil;
import com.pxwork.common.request.BackendLoginRequest;
import com.pxwork.common.utils.Result;
import com.pxwork.system.entity.AdminUser;
import com.pxwork.system.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    public Result<Map<String, Object>> login(@RequestBody @Validated BackendLoginRequest loginRequest) {
        String token = adminUserService.login(loginRequest);
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("tokenName", StpUtil.getTokenName());
        tokenInfo.put("tokenValue", token);
        return Result.success(tokenInfo);
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/user/info")
    public Result<AdminUser> userInfo() {
        long adminId = StpUtil.getLoginIdAsLong();
        AdminUser adminUser = adminUserService.getById(adminId);
        if (adminUser != null) {
            adminUser.setPassword(null);
        }
        return Result.success(adminUser);
    }

    @Operation(summary = "后台注销登录")
    @PostMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("注销成功");
    }
}
