package com.duocai.mall.service;

import com.duocai.mall.model.Orders;
import java.util.List;

/**
 * 订单服务接口
 * @author trae
 */
public interface OrderService {
    /**
     * 创建订单
     * @param userId 用户ID
     * @param addressId 收货地址ID
     * @param cartItemIds 购物车项ID列表
     * @return 创建成功的订单信息
     */
    Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds);
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单信息
     */
    Orders getOrderById(Long id);
    
    /**
     * 获取用户订单列表
     * @param userId 用户ID
     * @param status 订单状态，可选
     * @param page 页码
     * @param size 每页数量
     * @return 订单列表
     */
    List<Orders> getUserOrders(Long userId, Integer status, int page, int size);
    
    /**
     * 取消订单
     * @param id 订单ID
     * @param userId 用户ID
     * @return 是否取消成功
     */
    boolean cancelOrder(Long id, Long userId);
    
    /**
     * 支付订单
     * @param id 订单ID
     * @param userId 用户ID
     * @return 是否支付成功
     */
    boolean payOrder(Long id, Long userId);
    
    /**
     * 确认收货
     * @param id 订单ID
     * @param userId 用户ID
     * @return 是否确认成功
     */
    boolean confirmReceive(Long id, Long userId);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteOrder(Long id, Long userId);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 订单状态
     * @return 是否更新成功
     */
    boolean updateOrderStatus(Long id, Integer status);

    Orders updateOrder(Orders order);

    boolean confirmOrder(Long id);
}