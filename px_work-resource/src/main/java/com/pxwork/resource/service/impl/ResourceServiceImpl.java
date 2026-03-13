package com.pxwork.resource.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.resource.entity.Resource;
import com.pxwork.resource.mapper.ResourceMapper;
import com.pxwork.resource.service.ResourceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 资源库表 服务实现类
 * </p>
 *
 * @author TraeAI
 * @since 2026-03-13
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

}
