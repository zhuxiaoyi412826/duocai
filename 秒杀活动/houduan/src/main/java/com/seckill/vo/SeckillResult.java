package com.seckill.vo;

import com.seckill.entity.SeckillOrder;
import lombok.Data;

/**
 * 秒杀结果VO
 */
@Data
public class SeckillResult {
    private boolean success;
    private String message;
    private SeckillOrder seckillOrder;

    public static SeckillResult success(SeckillOrder seckillOrder) {
        SeckillResult result = new SeckillResult();
        result.setSuccess(true);
        result.setMessage("秒杀成功");
        result.setSeckillOrder(seckillOrder);
        return result;
    }

    public static SeckillResult fail(String message) {
        SeckillResult result = new SeckillResult();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}