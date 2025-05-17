package com.duocai.mall.service.impl;

import com.duocai.mall.mapper.ProductMapper;
import com.duocai.mall.model.Products;
import com.duocai.mall.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品服务实现类
 * @author trae
 */
@Service
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    
    public ProductServiceImpl(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Products createProduct(Products product) {
        // 设置商品初始状态和创建时间
        product.setStatus(1);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        
        // 保存商品信息
        productMapper.insert(product);
        return product;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Products updateProduct(Products product) {
        // 检查商品是否存在
        Products existingProduct = productMapper.selectById(product.getId());
        if (existingProduct == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品信息
        product.setUpdatedAt(LocalDateTime.now());
        productMapper.update(product);
        return productMapper.selectById(product.getId());
    }
    
    @Override
    public Products getProductById(Long id) {
        return productMapper.selectById(id);
    }
    
    @Override
    public List<Products> getProducts(Long categoryId, String keyword, int page, int size) {
        return productMapper.selectProducts(categoryId, keyword, (page - 1) * size, size);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteProduct(Long id) {
        return productMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStock(Long id, Integer quantity) {
        // 检查商品是否存在
        Products product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查库存是否充足
        if (product.getStock() + quantity < 0) {
            throw new RuntimeException("商品库存不足");
        }
        
        // 更新库存
        return productMapper.updateStock(id, quantity) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, Integer status) {
        // 检查商品是否存在
        Products product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 更新商品状态
        return productMapper.updateStatus(id, status) > 0;
    }
}