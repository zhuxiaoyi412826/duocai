package com.seckill.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillProductVO {

    /**
     * 秒杀商品ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 折扣率
     */
    private BigDecimal discountRate;

    /**
     * 总库存
     */
    private Integer totalStock;

    /**
     * 剩余库存
     */
    private Integer availableStock;

    /**
     * 限购数量
     */
    private Integer limitNum;

    /**
     * 已售数量
     */
    private Integer soldNum;

    /**
     * 商品状态：0-待开始，1-进行中，2-已结束，3-已售罄
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}