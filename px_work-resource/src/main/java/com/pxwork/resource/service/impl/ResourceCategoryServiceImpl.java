package com.pxwork.resource.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pxwork.resource.entity.ResourceCategory;
import com.pxwork.resource.mapper.ResourceCategoryMapper;
import com.pxwork.resource.service.ResourceCategoryService;

@Service
public class ResourceCategoryServiceImpl extends ServiceImpl<ResourceCategoryMapper, ResourceCategory>
        implements ResourceCategoryService {

    @Override
    public List<ResourceCategory> listTree() {
        List<ResourceCategory> allCategories = this.list(new LambdaQueryWrapper<ResourceCategory>()
                .orderByAsc(ResourceCategory::getSort)
                .orderByDesc(ResourceCategory::getCreatedAt));
        return buildTree(allCategories, 0L);
    }

    private List<ResourceCategory> buildTree(List<ResourceCategory> allList, Long parentId) {
        List<ResourceCategory> tree = new ArrayList<>();
        for (ResourceCategory category : allList) {
            if (category.getParentId().equals(parentId)) {
                category.setChildren(buildTree(allList, category.getId()));
                tree.add(category);
            }
        }
        return tree;
    }
}
