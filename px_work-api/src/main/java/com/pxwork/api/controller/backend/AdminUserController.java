package com.pxwork.api.controller.backend;

import com.pxwork.common.entity.AdminUser;
import com.pxwork.common.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 后台管理员 前端控制器
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-12
 */
@RestController
@RequestMapping("/admin-user")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/test")
    public List<AdminUser> test() {
        return adminUserService.list();
    }
}
