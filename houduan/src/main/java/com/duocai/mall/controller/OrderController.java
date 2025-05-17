package com.duocai.mall.controller;

import com.duocai.mall.model.Orders;
import com.duocai.mall.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    /**
     * 创建订单
     * @param order 订单信息
     * @return 创建成功的订单信息
     */
    @PostMapping("/orders")
    public ResponseEntity<?> createOrder(@RequestBody Orders order, @RequestParam Long addressId, @RequestParam List<Long> cartItemIds) {
        Orders createdOrder = orderService.createOrder(order.getUserId(), addressId, cartItemIds);
        return ResponseEntity.ok(createdOrder);
    }
    
    /**
     * 更新订单信息
     * @param id 订单ID
     * @param order 订单信息
     * @return 更新后的订单信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable Long id, @RequestBody Orders order) {
        order.setId(id);
        Orders updatedOrder = orderService.updateOrder(order);
        return ResponseEntity.ok(updatedOrder);
    }
    
    /**
     * 获取订单详情
     * @param id 订单ID
     * @return 订单信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id) {
        Orders order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }
    
    /**
     * 获取订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @param page 页码
     * @param size 每页数量
     * @return 订单列表
     */
    @GetMapping
    public ResponseEntity<?> getOrders(
            @RequestParam Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<Orders> orders = orderService.getUserOrders(userId, status, page, size);
        return ResponseEntity.ok(orders);
    }
    
    /**
     * 取消订单
     * @param id 订单ID
     * @return 取消结果
     */
    @PutMapping("/orders/{id}/cancel")
    public ResponseEntity<?> cancelOrder(@PathVariable Long id, @RequestParam Long userId) {
        boolean success = orderService.cancelOrder(id, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 支付订单
     * @param id 订单ID
     * @param paymentInfo 支付信息
     * @return 支付结果
     */
    @PutMapping("/orders/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id, @RequestParam Long userId, @RequestBody Map<String, Object> paymentInfo) {
        boolean success = orderService.payOrder(id, userId);
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 确认订单
     * @param id 订单ID
     * @return 确认结果
     */
    @PutMapping("/items/{id}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long id) {
        boolean success = orderService.confirmOrder(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
}