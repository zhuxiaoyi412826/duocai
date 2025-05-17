package com.duocai.mall.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 优惠券实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupons {
    private Long id; // 优惠券ID
    private String name; // 优惠券名称
    private String type; // 类型(满减/折扣)
    private BigDecimal amount; // 优惠金额/折扣率
    private BigDecimal minAmount; // 最低消费金额
    private Date startTime; // 开始时间
    private Date endTime; // 结束时间
    private Integer totalCount; // 发行数量
    private Integer remainingCount; // 剩余数量
    private Integer status; // 状态(0禁用1启用)
}