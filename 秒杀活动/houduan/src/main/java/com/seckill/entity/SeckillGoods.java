package com.seckill.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 秒杀商品实体类
 */
@Data
public class SeckillGoods {
    private Long id;
    private Long goodsId; // 商品ID
    private String goodsName; // 商品名称
    private String goodsImg; // 商品图片
    private BigDecimal goodsPrice; // 商品原价
    private BigDecimal seckillPrice; // 秒杀价
    private Integer stockCount; // 库存数量
    private Date startTime; // 秒杀开始时间
    private Date endTime; // 秒杀结束时间
    private Integer status; // 状态：0-未开始，1-进行中，2-已结束
    private Date createTime;
    private Date updateTime;
}