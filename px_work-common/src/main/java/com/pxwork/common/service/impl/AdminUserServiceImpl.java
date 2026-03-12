package com.pxwork.common.service.impl;

import com.pxwork.common.entity.AdminUser;
import com.pxwork.common.mapper.AdminUserMapper;
import com.pxwork.common.service.AdminUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 后台管理员表 服务实现类
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-12
 */
@Service
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {

}
