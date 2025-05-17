package com.duocai.mall.service.impl;

import com.duocai.mall.mapper.CategoryMapper;
import com.duocai.mall.model.Categories;
import com.duocai.mall.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品分类服务实现类
 * @author trae
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Categories createCategory(Categories category) {
        // 设置分类初始状态和创建时间
        category.setStatus(1);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        
        // 如果是子分类，设置层级
        if (category.getParentId() != null && category.getParentId() > 0) {
            Categories parentCategory = categoryMapper.selectById(category.getParentId());
            if (parentCategory == null) {
                throw new RuntimeException("父分类不存在");
            }
            category.setLevel(parentCategory.getLevel() + 1);
        } else {
            category.setParentId(0L);
            category.setLevel(1);
        }
        
        // 保存分类信息
        categoryMapper.insert(category);
        return category;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Categories updateCategory(Categories category) {
        // 检查分类是否存在
        Categories existingCategory = categoryMapper.selectById(category.getId());
        if (existingCategory == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 更新分类信息
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.update(category);
        return categoryMapper.selectById(category.getId());
    }
    
    @Override
    public Categories getCategoryById(Long id) {
        return categoryMapper.selectById(id);
    }
    
    @Override
    public List<Categories> getCategories(Long parentId) {
        return categoryMapper.selectByParentId(parentId);
    }
    
    @Override
    public List<Categories> getCategoryTree() {
        // 获取所有分类
        List<Categories> allCategories = categoryMapper.selectAll();
        
        // 构建分类树
        Map<Long, List<Categories>> categoryMap = allCategories.stream()
                .collect(Collectors.groupingBy(Categories::getParentId));
        
        // 获取一级分类
        List<Categories> rootCategories = categoryMap.getOrDefault(0L, new ArrayList<>());
        
        // 递归构建子分类
        rootCategories.forEach(category -> buildSubCategories(category, categoryMap));
        
        return rootCategories;
    }
    
    /**
     * 递归构建子分类
     * @param parent 父分类
     * @param categoryMap 分类映射
     */
    private void buildSubCategories(Categories parent, Map<Long, List<Categories>> categoryMap) {
        List<Categories> children = categoryMap.get(parent.getId());
        if (children != null) {
            children.forEach(child -> buildSubCategories(child, categoryMap));
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        // 检查是否存在子分类
        List<Categories> children = categoryMapper.selectByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子分类，无法删除");
        }
        
        return categoryMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查分类是否存在
        Categories category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 更新分类状态
        return categoryMapper.updateStatus(id, status) > 0;
    }
}