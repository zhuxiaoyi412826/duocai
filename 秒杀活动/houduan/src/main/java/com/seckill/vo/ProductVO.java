package com.seckill.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品详情VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {

    /**
     * 商品ID
     */
    private Long id;

    /**
     * 商品名称
     */
    private String name;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品图片
     */
    private String image;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品库存
     */
    private Integer stock;

    /**
     * 商品状态：0-下架，1-上架
     */
    private Integer status;

    /**
     * 是否参与秒杀：true-参与，false-不参与
     */
    private Boolean inSeckill;

    /**
     * 秒杀价格（如果参与秒杀）
     */
    private BigDecimal seckillPrice;

    /**
     * 秒杀开始时间（如果参与秒杀）
     */
    private LocalDateTime seckillStartTime;

    /**
     * 秒杀结束时间（如果参与秒杀）
     */
    private LocalDateTime seckillEndTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}