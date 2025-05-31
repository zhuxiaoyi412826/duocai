package com.seckill.mapper;

import com.seckill.entity.SeckillOrder;
import org.apache.ibatis.annotations.Param;

public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    
    /**
     * 根据用户ID和商品ID查询订单
     * @param userId 用户ID
     * @param goodsId 商品ID
     * @return 秒杀订单信息
     */
    SeckillOrder selectByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);
    
    /**
     * 更新订单状态
     * @param id 订单ID
     * @param status 状态
     * @param payTime 支付时间
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status, @Param("payTime") java.util.Date payTime);
}