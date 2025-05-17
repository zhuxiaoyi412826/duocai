package com.duocai.mall.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.math.BigDecimal;

/**
 * 订单实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Orders {
    private Long id;
    private Long userId;
    private String orderNo;
    private BigDecimal totalAmount;
    private Integer status; // 0: 待付款, 1: 待发货, 2: 待收货, 3: 已完成, 4: 已取消
    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String note;
    private LocalDateTime payTime;
    private LocalDateTime deliveryTime;
    private LocalDateTime receiveTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItems> orderItems;

}