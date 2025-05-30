package com.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckill.entity.SeckillProduct;
import com.seckill.vo.SeckillProductVO;

import java.util.List;

/**
 * 秒杀商品服务接口
 */
public interface SeckillProductService {
    
    /**
     * 创建秒杀商品
     *
     * @param seckillProduct 秒杀商品信息
     * @return 创建结果
     */
    boolean createSeckillProduct(SeckillProduct seckillProduct);
    
    /**
     * 更新秒杀商品信息
     *
     * @param seckillProduct 秒杀商品信息
     * @return 更新结果
     */
    boolean updateSeckillProduct(SeckillProduct seckillProduct);
    
    /**
     * 删除秒杀商品
     *
     * @param id 秒杀商品ID
     * @return 删除结果
     */
    boolean deleteSeckillProduct(Long id);
    
    /**
     * 根据活动ID删除秒杀商品
     *
     * @param activityId 活动ID
     * @return 删除结果
     */
    boolean deleteByActivityId(Long activityId);
    
    /**
     * 获取秒杀商品详情
     *
     * @param id 秒杀商品ID
     * @return 商品详情
     */
    SeckillProductVO getSeckillProductDetail(Long id);
    
    /**
     * 分页查询活动下的秒杀商品
     *
     * @param activityId 活动ID
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页数据
     */
    IPage<SeckillProductVO> getSeckillProductsPage(Long activityId, Integer page, Integer size);
    
    /**
     * 查询活动下的所有秒杀商品
     *
     * @param activityId 活动ID
     * @return 商品列表
     */
    List<SeckillProductVO> getSeckillProductsByActivityId(Long activityId);
    
    /**
     * 扣减秒杀商品库存
     *
     * @param id 秒杀商品ID
     * @param quantity 扣减数量
     * @return 扣减结果
     */
    boolean deductStock(Long id, Integer quantity);
    
    /**
     * 恢复秒杀商品库存
     *
     * @param id 秒杀商品ID
     * @param quantity 恢复数量
     * @return 恢复结果
     */
    boolean restoreStock(Long id, Integer quantity);
    
    /**
     * 更新秒杀商品状态
     *
     * @param id 秒杀商品ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateStatus(Long id, Integer status);
    
    /**
     * 查询商品在指定活动中的秒杀信息
     *
     * @param productId 商品ID
     * @param activityId 活动ID
     * @return 秒杀商品信息
     */
    SeckillProductVO getSeckillProductByProductIdAndActivityId(Long productId, Long activityId);
    
    /**
     * 检查秒杀商品是否可购买
     *
     * @param id 秒杀商品ID
     * @return 检查结果
     */
    boolean checkAvailable(Long id);
    
    /**
     * 获取秒杀商品当前库存
     *
     * @param id 秒杀商品ID
     * @return 库存数量
     */
    Integer getCurrentStock(Long id);
    
    /**
     * 预热活动商品信息到Redis
     *
     * @param activityId 活动ID
     */
    void preloadProductsForActivity(Long activityId);
}