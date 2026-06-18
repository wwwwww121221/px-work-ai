package com.pxwork.api.controller.backend;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pxwork.common.request.BackendLoginRequest;
import com.pxwork.common.utils.Result;
import com.pxwork.system.entity.AdminUser;
import com.pxwork.system.service.AdminUserService;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "1.0 后台-登录与认证")
@RestController
@RequestMapping("/backend")
@RequiredArgsConstructor
public class BackendLoginController {

    private final AdminUserService adminUserService;

    private final StpInterface stpInterface;

    @Operation(summary = "后台管理员登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody @Validated BackendLoginRequest loginRequest) {
        try {
            String token = adminUserService.login(loginRequest);
            long adminId = StpUtil.getLoginIdAsLong();
            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("tokenName", StpUtil.getTokenName());
            tokenInfo.put("tokenValue", token);
            tokenInfo.put("adminId", adminId);
            tokenInfo.put("roles", stpInterface.getRoleList(adminId, "login"));
            tokenInfo.put("permissions", stpInterface.getPermissionList(adminId, "login"));
            return Result.success(tokenInfo);
        } catch (RuntimeException e) {
            return Result.fail(e.getMessage());
        } catch (Exception e) {
            return Result.fail("登录失败，请稍后重试");
        }
    }

    @Operation(summary = "获取当前管理员信息")
    @GetMapping("/user/info")
    public Result<Map<String, Object>> userInfo() {
        long adminId = StpUtil.getLoginIdAsLong();
        AdminUser adminUser = adminUserService.getById(adminId);
        if (adminUser != null) {
            adminUser.setPassword(null);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("user", adminUser);
        result.put("roles", stpInterface.getRoleList(adminId, "login"));
        result.put("permissions", stpInterface.getPermissionList(adminId, "login"));
        return Result.success(result);
    }

    @Operation(summary = "后台注销登录")
    @DeleteMapping("/logout")
    public Result<String> logout() {
        StpUtil.logout();
        return Result.success("注销成功");
    }
}
