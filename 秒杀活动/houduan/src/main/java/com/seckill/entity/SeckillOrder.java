package com.seckill.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀订单实体类
 */
@Data
public class SeckillOrder {
    private Long id;
    private Long userId; // 用户ID
    private Long goodsId; // 商品ID
    private Long orderId; // 订单ID
    private BigDecimal seckillPrice; // 秒杀价格
    private Integer quantity; // 购买数量
    private Integer status; // 订单状态：0-未支付，1-已支付，2-已取消，3-已退款
    private Date createTime;
    private Date payTime; // 支付时间
}