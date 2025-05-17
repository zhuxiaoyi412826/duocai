package com.duocai.mall.service;

import com.duocai.mall.model.Products;
import java.util.List;

/**
 * 商品服务接口
 * @author trae
 */
public interface ProductService {
    /**
     * 创建商品
     * @param product 商品信息
     * @return 创建成功的商品信息
     */
    Products createProduct(Products product);
    
    /**
     * 更新商品信息
     * @param product 商品信息
     * @return 更新后的商品信息
     */
    Products updateProduct(Products product);
    
    /**
     * 根据ID获取商品信息
     * @param id 商品ID
     * @return 商品信息
     */
    Products getProductById(Long id);
    
    /**
     * 获取商品列表
     * @param categoryId 分类ID，可选
     * @param keyword 搜索关键词，可选
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表
     */
    List<Products> getProducts(Long categoryId, String keyword, int page, int size);
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 是否删除成功
     */
    boolean deleteProduct(Long id);
    
    /**
     * 更新商品库存
     * @param id 商品ID
     * @param quantity 变化数量，正数增加，负数减少
     * @return 是否更新成功
     */
    boolean updateStock(Long id, Integer quantity);
    
    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 商品状态
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, Integer status);
}