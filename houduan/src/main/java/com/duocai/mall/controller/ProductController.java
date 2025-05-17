package com.duocai.mall.controller;

import com.duocai.mall.model.Products;
import com.duocai.mall.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * 创建商品
     * @param product 商品信息
     * @return 创建成功的商品信息
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody Products product) {
        Products createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    /**
     * 更新商品信息
     * @param id 商品ID
     * @param product 商品信息
     * @return 更新后的商品信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Products product) {
        product.setId(id);
        Products updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        Products product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }
    
    /**
     * 获取商品列表
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页数量
     * @return 商品列表
     */
    @GetMapping
    public ResponseEntity<?> getProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Products> products = productService.getProducts(categoryId, keyword, page, size);
        return ResponseEntity.ok(products);
    }
    
    /**
     * 删除商品
     * @param id 商品ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        boolean success = productService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 商品状态
     * @return 更新结果
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> statusMap) {
        boolean success = productService.updateStatus(id, statusMap.get("status"));
        return ResponseEntity.ok(Map.of("success", success));
    }
}