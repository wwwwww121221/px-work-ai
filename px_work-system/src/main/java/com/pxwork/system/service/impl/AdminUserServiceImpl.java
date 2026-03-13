package com.pxwork.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.common.request.BackendLoginRequest;
import com.pxwork.system.entity.AdminUser;
import com.pxwork.system.entity.AdminUserRole;
import com.pxwork.system.mapper.AdminUserMapper;
import com.pxwork.system.service.AdminUserRoleService;
import com.pxwork.system.service.AdminUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

    @Autowired
    private AdminUserRoleService adminUserRoleService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createAdminUser(AdminUser adminUser) {
        if (StringUtils.isNotBlank(adminUser.getPassword())) {
            adminUser.setPassword(SaSecureUtil.sha256(adminUser.getPassword()));
        }
        boolean saved = this.save(adminUser);
        if (!saved) {
            return false;
        }

        saveRoles(adminUser.getId(), adminUser.getRoleIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateAdminUser(AdminUser adminUser) {
        // 1. 更新管理员基本信息
        boolean updated = this.updateById(adminUser);
        if (!updated) {
            return false;
        }

        // 2. 更新角色关联（如果 roleIds 不为 null）
        if (adminUser.getRoleIds() != null) {
            // 先删除旧关联
            adminUserRoleService.remove(new LambdaQueryWrapper<AdminUserRole>()
                    .eq(AdminUserRole::getAdminUserId, adminUser.getId()));
            // 再保存新关联
            saveRoles(adminUser.getId(), adminUser.getRoleIds());
        }
        return true;
    }

    @Override
    public String login(BackendLoginRequest request) {
        AdminUser adminUser = this.getOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getEmail, request.getEmail()));
        if (adminUser == null) {
            throw new RuntimeException("账号或密码错误");
        }
        String password = SaSecureUtil.sha256(request.getPassword());
        if (!password.equals(adminUser.getPassword())) {
            throw new RuntimeException("账号或密码错误");
        }
        StpUtil.login(adminUser.getId());
        return StpUtil.getTokenValue();
    }

    private void saveRoles(Long adminUserId, List<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            List<AdminUserRole> userRoles = roleIds.stream().map(roleId -> {
                AdminUserRole userRole = new AdminUserRole();
                userRole.setAdminUserId(adminUserId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());
            adminUserRoleService.saveBatch(userRoles);
        }
    }
}
