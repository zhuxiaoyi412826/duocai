package com.seckill.entity;

import lombok.Data;

import java.util.Date;

/**
 * 秒杀活动实体类
 */
@Data
public class SeckillActivity {
    private Long id;
    private String name; // 活动名称
    private String description; // 活动描述
    private Date startTime; // 活动开始时间
    private Date endTime; // 活动结束时间
    private Integer status; // 活动状态：0-未开始，1-进行中，2-已结束
    private Date createTime;
    private Date updateTime;
}