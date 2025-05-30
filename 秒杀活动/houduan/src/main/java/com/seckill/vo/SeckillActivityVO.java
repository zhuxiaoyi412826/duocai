package com.seckill.vo;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeckillActivityVO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动状态：0-未开始，1-进行中，2-已结束
     */
    private Integer status;

    /**
     * 活动状态描述
     */
    private String statusDesc;

    /**
     * 距离活动开始的剩余时间（秒）
     */
    private Long remainingSeconds;

    /**
     * 参与活动的商品列表
     */
    private List<SeckillProductVO> products;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}