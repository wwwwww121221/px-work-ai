package com.pxwork.system.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.system.entity.AdminMenu;
import com.pxwork.system.mapper.AdminMenuMapper;
import com.pxwork.system.service.AdminMenuService;

@Service
public class AdminMenuServiceImpl extends ServiceImpl<AdminMenuMapper, AdminMenu> implements AdminMenuService {

    @Override
    public List<AdminMenu> getMenuTree() {
        ensureDefaultMenus();
        List<AdminMenu> allMenus = this.list(new LambdaQueryWrapper<AdminMenu>()
                .orderByAsc(AdminMenu::getSort)
                .orderByAsc(AdminMenu::getId));
        return buildChildren(0L, allMenus);
    }

    private void ensureDefaultMenus() {
        List<AdminMenu> existingMenus = this.list();
        Set<Long> existingIds = new HashSet<>();
        Set<String> existingPerms = new HashSet<>();
        for (AdminMenu existingMenu : existingMenus) {
            if (existingMenu.getId() != null) {
                existingIds.add(existingMenu.getId());
            }
            String perms = trimToNull(existingMenu.getPerms());
            if (perms != null) {
                existingPerms.add(perms);
            }
        }

        List<AdminMenu> missingMenus = new ArrayList<>();
        for (AdminMenu menu : buildDefaultMenus()) {
            boolean existsById = menu.getId() != null && existingIds.contains(menu.getId());
            String perms = trimToNull(menu.getPerms());
            boolean existsByPerms = perms != null && existingPerms.contains(perms);
            if (!existsById && !existsByPerms) {
                missingMenus.add(menu);
            }
        }
        if (!missingMenus.isEmpty()) {
            this.saveBatch(missingMenus);
        }
    }

    private List<AdminMenu> buildDefaultMenus() {
        LocalDateTime now = LocalDateTime.now();
        List<AdminMenu> seed = new ArrayList<>();

        seed.add(menu(now, 1000L, 0L, "系统管理", null, 1, 10));
        seed.add(menu(now, 1001L, 1000L, "查看权限树", "system:menu:list", 3, 11));
        seed.add(menu(now, 1002L, 1000L, "角色列表", "system:role:list", 3, 12));
        seed.add(menu(now, 1003L, 1000L, "新增角色", "system:role:add", 3, 13));
        seed.add(menu(now, 1004L, 1000L, "修改角色", "system:role:update", 3, 14));
        seed.add(menu(now, 1005L, 1000L, "删除角色", "system:role:delete", 3, 15));
        seed.add(menu(now, 1006L, 1000L, "分配角色权限", "system:role:assign", 3, 16));
        seed.add(menu(now, 1010L, 1000L, "管理员列表", "system:admin:list", 3, 20));
        seed.add(menu(now, 1011L, 1000L, "新增管理员", "system:admin:add", 3, 21));
        seed.add(menu(now, 1012L, 1000L, "修改管理员", "system:admin:update", 3, 22));
        seed.add(menu(now, 1013L, 1000L, "删除管理员", "system:admin:delete", 3, 23));
        seed.add(menu(now, 1020L, 1000L, "学员列表", "system:user:list", 3, 30));
        seed.add(menu(now, 1021L, 1000L, "新增学员", "system:user:add", 3, 31));
        seed.add(menu(now, 1022L, 1000L, "修改学员", "system:user:update", 3, 32));
        seed.add(menu(now, 1023L, 1000L, "删除学员", "system:user:delete", 3, 33));
        seed.add(menu(now, 1030L, 1000L, "部门列表", "system:dept:list", 3, 40));
        seed.add(menu(now, 1031L, 1000L, "新增部门", "system:dept:add", 3, 41));
        seed.add(menu(now, 1032L, 1000L, "修改部门", "system:dept:update", 3, 42));
        seed.add(menu(now, 1033L, 1000L, "删除部门", "system:dept:delete", 3, 43));
        seed.add(menu(now, 1040L, 1000L, "字典列表", "system:dict:list", 3, 50));
        seed.add(menu(now, 1041L, 1000L, "新增字典", "system:dict:add", 3, 51));
        seed.add(menu(now, 1042L, 1000L, "修改字典", "system:dict:update", 3, 52));
        seed.add(menu(now, 1043L, 1000L, "删除字典", "system:dict:delete", 3, 53));

        seed.add(menu(now, 2000L, 0L, "课程管理", null, 1, 60));
        seed.add(menu(now, 2001L, 2000L, "课程列表", "course:list", 3, 61));
        seed.add(menu(now, 2002L, 2000L, "新增课程", "course:add", 3, 62));
        seed.add(menu(now, 2003L, 2000L, "修改课程", "course:update", 3, 63));
        seed.add(menu(now, 2004L, 2000L, "删除课程", "course:delete", 3, 64));
        seed.add(menu(now, 2005L, 2000L, "查询课程", "course:query", 3, 65));

        seed.add(menu(now, 3000L, 0L, "证书管理", null, 1, 70));
        seed.add(menu(now, 3001L, 3000L, "证书更新", "certificate:update", 3, 71));

        return seed;
    }

    private AdminMenu menu(LocalDateTime now, Long id, Long parentId, String name, String perms, Integer type, Integer sort) {
        return new AdminMenu()
                .setId(id)
                .setParentId(parentId)
                .setName(name)
                .setPerms(perms)
                .setType(type)
                .setSort(sort)
                .setCreatedAt(now)
                .setUpdatedAt(now);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private List<AdminMenu> buildChildren(Long parentId, List<AdminMenu> allMenus) {
        List<AdminMenu> children = new ArrayList<>();
        for (AdminMenu menu : allMenus) {
            Long menuParentId = menu.getParentId();
            if (!isSameParent(menuParentId, parentId)) {
                continue;
            }
            menu.setChildren(buildChildren(menu.getId(), allMenus));
            children.add(menu);
        }
        return children;
    }

    private boolean isSameParent(Long currentParentId, Long targetParentId) {
        if (currentParentId == null) {
            return targetParentId == null || targetParentId == 0L;
        }
        return currentParentId.equals(targetParentId);
    }
}
