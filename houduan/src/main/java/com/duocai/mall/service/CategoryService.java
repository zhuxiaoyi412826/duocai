package com.duocai.mall.service;

import com.duocai.mall.model.Categories;
import java.util.List;

/**
 * 商品分类服务接口
 * @author trae
 */
public interface CategoryService {
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建成功的分类信息
     */
    Categories createCategory(Categories category);
    
    /**
     * 更新分类信息
     * @param category 分类信息
     * @return 更新后的分类信息
     */
    Categories updateCategory(Categories category);
    
    /**
     * 根据ID获取分类信息
     * @param id 分类ID
     * @return 分类信息
     */
    Categories getCategoryById(Long id);
    
    /**
     * 获取分类列表
     * @param parentId 父分类ID，可选
     * @return 分类列表
     */
    List<Categories> getCategories(Long parentId);
    
    /**
     * 获取完整的分类树
     * @return 分类树列表
     */
    List<Categories> getCategoryTree();
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long id);
    
    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 分类状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
}