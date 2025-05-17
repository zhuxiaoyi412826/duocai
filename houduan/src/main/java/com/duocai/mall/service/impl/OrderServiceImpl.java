package com.duocai.mall.service.impl;

import com.duocai.mall.mapper.OrderMapper;
import com.duocai.mall.mapper.OrderItemMapper;
import com.duocai.mall.mapper.ProductMapper;
import com.duocai.mall.mapper.CartMapper;
import com.duocai.mall.model.Orders;
import com.duocai.mall.model.OrderItems;
import com.duocai.mall.model.Products;
import com.duocai.mall.model.CartItems;
import com.duocai.mall.service.OrderService;
import com.duocai.mall.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务实现类
 * @author trae
 */
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;
    private final CartMapper cartMapper;
    private final ProductService productService;
    
    public OrderServiceImpl(OrderMapper orderMapper,
                          OrderItemMapper orderItemMapper,
                          ProductMapper productMapper,
                          CartMapper cartMapper,
                          ProductService productService) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.productMapper = productMapper;
        this.cartMapper = cartMapper;
        this.productService = productService;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders createOrder(Long userId, Long addressId, List<Long> cartItemIds) {
        // 获取购物车商品
        List<CartItems> cartItems = cartMapper.selectByIds(cartItemIds);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车商品不存在");
        }
        
        // 创建订单
        Orders order = new Orders();
        order.setUserId(userId);
        order.setOrderNo(generateOrderNo());
        order.setStatus(0); // 待付款
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        // 创建订单项并计算总金额
        List<OrderItems> orderItems = new ArrayList<>();
        for (CartItems cartItem : cartItems) {
            // 检查商品是否存在且有库存
            Products product = productMapper.selectById(cartItem.getProductId());
if (product == null) {
    throw new RuntimeException("商品不存在：" + cartItem.getProductId());
            }
            if (product.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("商品库存不足：" + product.getName());
            }
            
            // 创建订单项
            OrderItems orderItem = new OrderItems();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getImage());
            orderItem.setProductPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())));
            orderItem.setCreatedAt(LocalDateTime.now());
            orderItem.setUpdatedAt(LocalDateTime.now());
            
            orderItems.add(orderItem);
            
            // 更新商品库存
            productService.updateStock(product.getId(), -cartItem.getQuantity());
        }
        
        // 保存订单和订单项
        orderMapper.insert(order);
        for (OrderItems orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }
        
        // 删除购物车商品
        cartMapper.deleteByUserId(userId);
        
        // 设置订单项并返回
        order.setOrderItems(orderItems);
        return order;
    }
    
    @Override
    public Orders getOrderById(Long id) {
        Orders order = orderMapper.selectById(id);
        if (order != null) {
            List<OrderItems> orderItems = orderItemMapper.selectByOrderId(id);
            order.setOrderItems(orderItems);
        }
        return order;
    }
    
    @Override
    public List<Orders> getUserOrders(Long userId, Integer status, int page, int size) {
        List<Orders> orders = orderMapper.selectByUserIdAndStatus(userId, status, (page - 1) * size, size);
        for (Orders order : orders) {
            List<OrderItems> orderItems = orderItemMapper.selectByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        return orders;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long id, Long userId) {
        Orders order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不允许取消");
        }
        
        // 恢复商品库存
        List<OrderItems> orderItems = orderItemMapper.selectByOrderId(id);
        for (OrderItems orderItem : orderItems) {
            productService.updateStock(orderItem.getProductId(), orderItem.getQuantity());
        }
        
        // 更新订单状态
        return updateOrderStatus(id, 4);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long id, Long userId) {
        Orders order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(1); // 待发货
        order.setPayTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.update(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceive(Long id, Long userId) {
        Orders order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 2) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus(3); // 已完成
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.update(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteOrder(Long id, Long userId) {
        Orders order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        
        if (order.getStatus() != 3 && order.getStatus() != 4) {
            throw new RuntimeException("订单状态不允许删除");
        }
        
        // 删除订单项
        orderItemMapper.deleteByOrderId(id);
        
        // 删除订单
        return orderMapper.deleteById(id) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Orders updateOrder(Orders order) {
        // 检查订单是否存在
        Orders existingOrder = orderMapper.selectById(order.getId());
        if (existingOrder == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 更新订单信息
        order.setUpdatedAt(LocalDateTime.now());
        orderMapper.update(order);
        
        return order;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmOrder(Long id) {
        // 检查订单是否存在
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 确认订单逻辑
        if (order.getStatus() != 1) {
            throw new RuntimeException("订单状态不正确");
        }
        
        // 更新订单状态为已确认（假设状态码2表示已发货）
        order.setStatus(2);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.update(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long id, Integer status) {
        Orders order = orderMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        return orderMapper.update(order) > 0;
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNo() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}