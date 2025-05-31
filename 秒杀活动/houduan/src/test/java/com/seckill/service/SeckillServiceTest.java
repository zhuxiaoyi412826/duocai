package com.seckill.service;

import com.seckill.SeckillApplication;
import com.seckill.entity.SeckillGoods;
import com.seckill.entity.SeckillOrder;
import com.seckill.vo.SeckillResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = SeckillApplication.class)
class SeckillServiceTest {

    @Autowired
    private SeckillService seckillService;

    @Test
    void testExecuteSeckill() {
        // 准备测试数据
        Long userId = 10001L;
        Long goodsId = 1001L;

        // 执行秒杀
        SeckillResult result = seckillService.executeSeckill(userId, goodsId);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getSeckillOrder());
        assertEquals(userId, result.getSeckillOrder().getUserId());
        assertEquals(goodsId, result.getSeckillOrder().getGoodsId());
    }

    @Test
    void testGetSeckillResult() {
        // 准备测试数据
        Long userId = 10001L;
        Long goodsId = 1001L;

        // 获取秒杀结果
        SeckillResult result = seckillService.getSeckillResult(userId, goodsId);

        // 验证结果
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getSeckillOrder());
    }

    @Test
    void testCheckStock() {
        // 准备测试数据
        Long goodsId = 1001L;

        // 检查库存
        boolean hasStock = seckillService.checkStock(goodsId);

        // 验证结果
        assertTrue(hasStock);
    }
}