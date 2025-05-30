package com.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckill.dto.SeckillRequestDTO;
import com.seckill.entity.SeckillOrder;
import com.seckill.vo.SeckillOrderVO;

import java.util.List;

/**
 * 秒杀订单服务接口
 */
public interface SeckillOrderService {
    
    /**
     * 创建秒杀订单
     *
     * @param request 秒杀请求信息
     * @return 订单ID
     */
    Long createOrder(SeckillRequestDTO request);
    
    /**
     * 取消订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 取消结果
     */
    boolean cancelOrder(Long orderId, Long userId);
    
    /**
     * 支付订单
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 支付结果
     */
    boolean payOrder(Long orderId, Long userId);
    
    /**
     * 获取订单详情
     *
     * @param orderId 订单ID
     * @return 订单详情
     */
    SeckillOrderVO getOrderDetail(Long orderId);
    
    /**
     * 获取用户的订单列表
     *
     * @param userId 用户ID
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页数据
     */
    IPage<SeckillOrderVO> getUserOrders(Long userId, Integer page, Integer size);
    
    /**
     * 获取用户在指定活动中的订单
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 订单列表
     */
    List<SeckillOrderVO> getUserOrdersByActivity(Long userId, Long activityId);
    
    /**
     * 处理超时未支付订单
     */
    void handleTimeoutOrders();
    
    /**
     * 检查用户是否已经购买过指定商品
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param productId 商品ID
     * @return 检查结果
     */
    boolean checkUserAlreadyPurchased(Long userId, Long activityId, Long productId);
    
    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @return 更新结果
     */
    boolean updateOrderStatus(Long orderId, Integer oldStatus, Integer newStatus);
    
    /**
     * 获取活动的订单统计信息
     *
     * @param activityId 活动ID
     * @return 订单统计信息
     */
    SeckillOrderVO getActivityOrderStats(Long activityId);
    
    /**
     * 获取用户的订单数量
     *
     * @param userId 用户ID
     * @param status 订单状态（可选）
     * @return 订单数量
     */
    Integer getUserOrderCount(Long userId, Integer status);
    
    /**
     * 验证订单是否属于指定用户
     *
     * @param orderId 订单ID
     * @param userId 用户ID
     * @return 验证结果
     */
    boolean verifyOrderBelongsToUser(Long orderId, Long userId);
}