package com.seckill.service.impl;

import com.seckill.entity.SeckillGoods;
import com.seckill.entity.SeckillOrder;
import com.seckill.mapper.SeckillGoodsMapper;
import com.seckill.mapper.SeckillOrderMapper;
import com.seckill.service.SeckillService;
import com.seckill.vo.SeckillResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public SeckillGoods getSeckillGoodsDetail(Long goodsId) {
        return seckillGoodsMapper.selectById(goodsId);
    }

    @Override
    @Transactional
    public SeckillResult executeSeckill(Long userId, Long goodsId) {
        // 1. 检查库存
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        if (goods == null || goods.getStockCount() <= 0) {
            return SeckillResult.fail("商品已售罄");
        }

        // 2. 检查是否已经秒杀过
        SeckillOrder order = seckillOrderMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (order != null) {
            return SeckillResult.fail("不能重复秒杀");
        }

        // 3. 减库存
        int affectedRows = seckillGoodsMapper.reduceStock(goodsId, 1);
        if (affectedRows == 0) {
            return SeckillResult.fail("秒杀失败");
        }

        // 4. 创建订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(userId);
        seckillOrder.setGoodsId(goodsId);
        seckillOrder.setSeckillPrice(goods.getSeckillPrice());
        seckillOrder.setQuantity(1);
        seckillOrder.setStatus(0); // 未支付
        seckillOrder.setCreateTime(new Date());
        seckillOrderMapper.insert(seckillOrder);

        return SeckillResult.success(seckillOrder);
    }

    @Override
    public SeckillResult getSeckillResult(Long userId, Long goodsId) {
        SeckillOrder order = seckillOrderMapper.selectByUserIdAndGoodsId(userId, goodsId);
        if (order == null) {
            return SeckillResult.fail("秒杀失败");
        }
        return SeckillResult.success(order);
    }

    @Override
    public SeckillOrder createSeckillOrder(Long userId, Long goodsId) {
        // 实际项目中这里会有更复杂的逻辑
        return null;
    }

    @Override
    public boolean checkStock(Long goodsId) {
        SeckillGoods goods = seckillGoodsMapper.selectById(goodsId);
        return goods != null && goods.getStockCount() > 0;
    }
}