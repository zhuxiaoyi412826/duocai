package com.seckill.mapper;

import com.seckill.entity.SeckillGoods;
import org.apache.ibatis.annotations.Param;

public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {
    
    /**
     * 减库存
     * @param goodsId 商品ID
     * @param quantity 减少数量
     * @return 影响行数
     */
    int reduceStock(@Param("goodsId") Long goodsId, @Param("quantity") Integer quantity);
    
    /**
     * 根据商品ID查询秒杀商品
     * @param goodsId 商品ID
     * @return 秒杀商品信息
     */
    SeckillGoods selectByGoodsId(@Param("goodsId") Long goodsId);
}