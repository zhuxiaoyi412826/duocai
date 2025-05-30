package com.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckill.entity.Product;
import com.seckill.vo.ProductVO;

import java.util.List;

/**
 * 商品服务接口
 */
public interface ProductService {
    
    /**
     * 创建商品
     *
     * @param product 商品信息
     * @return 创建结果
     */
    boolean createProduct(Product product);
    
    /**
     * 更新商品信息
     *
     * @param product 商品信息
     * @return 更新结果
     */
    boolean updateProduct(Product product);
    
    /**
     * 删除商品
     *
     * @param productId 商品ID
     * @return 删除结果
     */
    boolean deleteProduct(Long productId);
    
    /**
     * 获取商品详情
     *
     * @param productId 商品ID
     * @return 商品详情
     */
    ProductVO getProductDetail(Long productId);
    
    /**
     * 分页查询在售商品
     *
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页数据
     */
    IPage<ProductVO> getOnSaleProducts(Integer page, Integer size);
    
    /**
     * 查询指定类目下的商品
     *
     * @param categoryId 类目ID
     * @return 商品列表
     */
    List<ProductVO> getProductsByCategory(Long categoryId);
    
    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页数据
     */
    IPage<ProductVO> searchProducts(String keyword, Integer page, Integer size);
    
    /**
     * 更新商品库存
     *
     * @param productId 商品ID
     * @param stock 库存变化量（正数增加，负数减少）
     * @return 更新结果
     */
    boolean updateStock(Long productId, Integer stock);
    
    /**
     * 扣减商品库存（乐观锁）
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @return 扣减结果
     */
    boolean deductStock(Long productId, Integer quantity);
    
    /**
     * 批量获取商品信息
     *
     * @param productIds 商品ID列表
     * @return 商品信息列表
     */
    List<ProductVO> getProductsByIds(List<Long> productIds);
    
    /**
     * 检查商品是否存在且在售
     *
     * @param productId 商品ID
     * @return 检查结果
     */
    boolean checkProductAvailable(Long productId);
    
    /**
     * 获取商品当前库存
     *
     * @param productId 商品ID
     * @return 库存数量
     */
    Integer getCurrentStock(Long productId);
}