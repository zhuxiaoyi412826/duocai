package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.dto.SeckillRequestDTO;
import com.seckill.entity.SeckillActivity;
import com.seckill.entity.SeckillOrder;
import com.seckill.entity.SeckillProduct;
import com.seckill.mapper.SeckillOrderMapper;
import com.seckill.service.SeckillActivityService;
import com.seckill.service.SeckillOrderService;
import com.seckill.service.SeckillProductService;
import com.seckill.service.UserService;
import com.seckill.vo.SeckillOrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 秒杀订单服务实现类
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillOrderMapper orderMapper;

    @Autowired
    private SeckillActivityService activityService;

    @Autowired
    private SeckillProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ORDER_LOCK_PREFIX = "seckill:order:lock:";
    private static final String USER_PURCHASE_PREFIX = "seckill:purchase:";
    private static final int ORDER_PAYMENT_TIMEOUT_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SeckillRequestDTO request) {
        Long userId = request.getUserId();
        Long activityId = request.getActivityId();
        Long productId = request.getProductId();
        Integer quantity = request.getQuantity();

        // 检查活动是否有效
        if (!activityService.checkActivityOngoing(activityId)) {
            throw new RuntimeException("活动不存在或未开始");
        }

        // 获取秒杀商品信息
        SeckillProduct seckillProduct = productService.getSeckillProductByProductIdAndActivityId(productId, activityId).toEntity();
        if (seckillProduct == null || seckillProduct.getStatus() != 1) {
            throw new RuntimeException("秒杀商品不存在或已下架");
        }

        // 检查库存
        if (seckillProduct.getAvailableStock() < quantity) {
            throw new RuntimeException("商品库存不足");
        }

        // 检查用户是否已购买过（限购）
        if (seckillProduct.getLimitPerUser() > 0) {
            String purchaseKey = USER_PURCHASE_PREFIX + activityId + ":" + productId + ":" + userId;
            Integer purchased = (Integer) redisTemplate.opsForValue().get(purchaseKey);
            if (purchased != null && purchased >= seckillProduct.getLimitPerUser()) {
                throw new RuntimeException("已达到该商品购买上限");
            }
        }

        // 获取分布式锁，防止同一用户并发下单
        String lockKey = ORDER_LOCK_PREFIX + userId + ":" + activityId + ":" + productId;
        String lockValue = UUID.randomUUID().toString();
        boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 10, TimeUnit.SECONDS);
        if (!locked) {
            throw new RuntimeException("请勿重复提交订单");
        }

        try {
            // 扣减库存
            boolean deductResult = productService.deductStock(seckillProduct.getId(), quantity);
            if (!deductResult) {
                throw new RuntimeException("扣减库存失败");
            }

            // 创建订单
            SeckillOrder order = new SeckillOrder();
            order.setUserId(userId);
            order.setActivityId(activityId);
            order.setProductId(productId);
            order.setSeckillProductId(seckillProduct.getId());
            order.setQuantity(quantity);
            order.setOrderAmount(seckillProduct.getSeckillPrice().multiply(new BigDecimal(quantity)));
            order.setStatus(0); // 0-待支付
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            order.setPaymentDeadline(LocalDateTime.now().plusMinutes(ORDER_PAYMENT_TIMEOUT_MINUTES));
            
            // 保存订单
            save(order);

            // 记录用户购买数量
            if (seckillProduct.getLimitPerUser() > 0) {
                String purchaseKey = USER_PURCHASE_PREFIX + activityId + ":" + productId + ":" + userId;
                redisTemplate.opsForValue().increment(purchaseKey, quantity);
                // 设置过期时间与活动一致
                SeckillActivity activity = activityService.getActivityDetail(activityId).toEntity();
                if (activity != null) {
                    long seconds = java.time.Duration.between(LocalDateTime.now(), activity.getEndTime()).getSeconds();
                    if (seconds > 0) {
                        redisTemplate.expire(purchaseKey, seconds, TimeUnit.SECONDS);
                    }
                }
            }

            return order.getId();
        } finally {
            // 释放锁
            if (lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
                redisTemplate.delete(lockKey);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId, Long userId) {
        // 验证订单归属
        if (!verifyOrderBelongsToUser(orderId, userId)) {
            throw new RuntimeException("订单不存在或不属于当前用户");
        }

        // 获取订单信息
        SeckillOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("只能取消待支付订单");
        }

        // 更新订单状态为已取消
        boolean updateResult = updateOrderStatus(orderId, 0, 3);
        if (!updateResult) {
            throw new RuntimeException("取消订单失败");
        }

        // 恢复库存
        productService.restoreStock(order.getSeckillProductId(), order.getQuantity());

        // 恢复用户购买限制
        String purchaseKey = USER_PURCHASE_PREFIX + order.getActivityId() + ":" + order.getProductId() + ":" + userId;
        redisTemplate.opsForValue().decrement(purchaseKey, order.getQuantity());

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId, Long userId) {
        // 验证订单归属
        if (!verifyOrderBelongsToUser(orderId, userId)) {
            throw new RuntimeException("订单不存在或不属于当前用户");
        }

        // 获取订单信息
        SeckillOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new RuntimeException("订单状态异常，无法支付");
        }

        // 检查支付截止时间
        if (order.getPaymentDeadline().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("订单已超时，请重新下单");
        }

        // 模拟支付流程（实际项目中应对接支付网关）
        
        // 更新订单状态为已支付
        boolean updateResult = updateOrderStatus(orderId, 0, 1);
        if (!updateResult) {
            throw new RuntimeException("支付失败，请重试");
        }

        // 更新支付时间
        order.setPayTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        updateById(order);

        return true;
    }

    @Override
    public SeckillOrderVO getOrderDetail(Long orderId) {
        SeckillOrder order = getById(orderId);
        if (order == null) {
            return null;
        }
        return convertToVO(order);
    }

    @Override
    public IPage<SeckillOrderVO> getUserOrders(Long userId, Integer page, Integer size) {
        Page<SeckillOrder> orderPage = new Page<>(page, size);
        IPage<SeckillOrder> orderIPage = orderMapper.selectUserOrdersPage(userId, orderPage);
        return convertToVOPage(orderIPage);
    }

    @Override
    public List<SeckillOrderVO> getUserOrdersByActivity(Long userId, Long activityId) {
        List<SeckillOrder> orders = orderMapper.selectUserOrdersByActivity(userId, activityId);
        return orders.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleTimeoutOrders() {
        // 查询超时未支付的订单
        List<SeckillOrder> timeoutOrders = orderMapper.selectTimeoutOrders(LocalDateTime.now());
        
        for (SeckillOrder order : timeoutOrders) {
            // 更新订单状态为已取消
            boolean updateResult = updateOrderStatus(order.getId(), 0, 3);
            if (updateResult) {
                // 恢复库存
                productService.restoreStock(order.getSeckillProductId(), order.getQuantity());
                
                // 恢复用户购买限制
                String purchaseKey = USER_PURCHASE_PREFIX + order.getActivityId() + ":" + order.getProductId() + ":" + order.getUserId();
                redisTemplate.opsForValue().decrement(purchaseKey, order.getQuantity());
            }
        }
    }

    @Override
    public boolean checkUserAlreadyPurchased(Long userId, Long activityId, Long productId) {
        String purchaseKey = USER_PURCHASE_PREFIX + activityId + ":" + productId + ":" + userId;
        Object purchased = redisTemplate.opsForValue().get(purchaseKey);
        if (purchased != null && Integer.parseInt(purchased.toString()) > 0) {
            return true;
        }
        
        // Redis中没有记录，查询数据库
        return orderMapper.countUserPurchased(userId, activityId, productId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrderStatus(Long orderId, Integer oldStatus, Integer newStatus) {
        return orderMapper.updateStatus(orderId, oldStatus, newStatus, LocalDateTime.now()) > 0;
    }

    @Override
    public SeckillOrderVO getActivityOrderStats(Long activityId) {
        // 获取活动订单统计信息
        SeckillOrderVO stats = new SeckillOrderVO();
        stats.setTotalOrders(orderMapper.countActivityOrders(activityId));
        stats.setPaidOrders(orderMapper.countActivityOrdersByStatus(activityId, 1));
        stats.setCanceledOrders(orderMapper.countActivityOrdersByStatus(activityId, 3));
        
        // 计算支付转化率
        if (stats.getTotalOrders() > 0) {
            stats.setConversionRate(BigDecimal.valueOf(stats.getPaidOrders())
                    .divide(BigDecimal.valueOf(stats.getTotalOrders()), 4, BigDecimal.ROUND_HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, BigDecimal.ROUND_HALF_UP));
        } else {
            stats.setConversionRate(BigDecimal.ZERO);
        }
        
        return stats;
    }

    @Override
    public Integer getUserOrderCount(Long userId, Integer status) {
        return orderMapper.countUserOrders(userId, status);
    }

    @Override
    public boolean verifyOrderBelongsToUser(Long orderId, Long userId) {
        return orderMapper.countUserOrder(orderId, userId) > 0;
    }

    /**
     * 将订单实体转换为VO对象
     */
    private SeckillOrderVO convertToVO(SeckillOrder order) {
        if (order == null) {
            return null;
        }
        SeckillOrderVO orderVO = new SeckillOrderVO();
        BeanUtils.copyProperties(order, orderVO);
        return orderVO;
    }

    /**
     * 将订单分页数据转换为VO分页数据
     */
    private IPage<SeckillOrderVO> convertToVOPage(IPage<SeckillOrder> orderPage) {
        IPage<SeckillOrderVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<SeckillOrderVO> voList = orderPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }
}