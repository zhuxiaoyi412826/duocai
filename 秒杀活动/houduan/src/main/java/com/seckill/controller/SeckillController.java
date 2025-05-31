package com.seckill.controller;

import com.seckill.entity.SeckillGoods;
import com.seckill.service.SeckillService;
import com.seckill.vo.SeckillResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取秒杀商品详情
     */
    @GetMapping("/goods/{goodsId}")
    public SeckillGoods getSeckillGoodsDetail(@PathVariable Long goodsId) {
        return seckillService.getSeckillGoodsDetail(goodsId);
    }

    /**
     * 执行秒杀
     */
    @PostMapping("/execute/{goodsId}")
    public SeckillResult executeSeckill(@RequestHeader("userId") Long userId,
                                       @PathVariable Long goodsId) {
        return seckillService.executeSeckill(userId, goodsId);
    }

    /**
     * 获取秒杀结果
     */
    @GetMapping("/result/{goodsId}")
    public SeckillResult getSeckillResult(@RequestHeader("userId") Long userId,
                                        @PathVariable Long goodsId) {
        return seckillService.getSeckillResult(userId, goodsId);
    }

    /**
     * 检查库存
     */
    @GetMapping("/checkStock/{goodsId}")
    public boolean checkStock(@PathVariable Long goodsId) {
        return seckillService.checkStock(goodsId);
    }
}