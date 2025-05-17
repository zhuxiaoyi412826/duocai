package com.duocai.mall.controller;

import com.duocai.mall.model.Categories;
import com.duocai.mall.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品分类控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final CategoryService categoryService;
    
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    
    /**
     * 创建分类
     * @param category 分类信息
     * @return 创建成功的分类信息
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Categories category) {
        Categories createdCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(createdCategory);
    }
    
    /**
     * 更新分类信息
     * @param id 分类ID
     * @param category 分类信息
     * @return 更新后的分类信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @RequestBody Categories category) {
        category.setId(id);
        Categories updatedCategory = categoryService.updateCategory(category);
        return ResponseEntity.ok(updatedCategory);
    }
    
    /**
     * 获取分类详情
     * @param id 分类ID
     * @return 分类信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategory(@PathVariable Long id) {
        Categories category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
    
    /**
     * 获取分类列表
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @GetMapping
    public ResponseEntity<?> getCategories(@RequestParam(required = false) Long parentId) {
        List<Categories> categories = categoryService.getCategories(parentId);
        return ResponseEntity.ok(categories);
    }
    
    /**
     * 获取分类树
     * @return 分类树列表
     */
    @GetMapping("/tree")
    public ResponseEntity<?> getCategoryTree() {
        List<Categories> categoryTree = categoryService.getCategoryTree();
        return ResponseEntity.ok(categoryTree);
    }
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        boolean success = categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 分类状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        boolean success = categoryService.updateStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(Map.of("success", success));
    }
}