package com.seckill.service;

import com.seckill.entity.SeckillGoods;
import com.seckill.entity.SeckillOrder;
import com.seckill.vo.SeckillResult;

/**
 * 秒杀服务接口
 */
public interface SeckillService {

    /**
     * 获取秒杀商品详情
     */
    SeckillGoods getSeckillGoodsDetail(Long goodsId);

    /**
     * 执行秒杀
     */
    SeckillResult executeSeckill(Long userId, Long goodsId);

    /**
     * 获取秒杀结果
     */
    SeckillResult getSeckillResult(Long userId, Long goodsId);

    /**
     * 创建秒杀订单
     */
    SeckillOrder createSeckillOrder(Long userId, Long goodsId);

    /**
     * 检查秒杀库存
     */
    boolean checkStock(Long goodsId);
}