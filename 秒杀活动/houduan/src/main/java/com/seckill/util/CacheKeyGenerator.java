package com.seckill.util;

import org.springframework.stereotype.Component;

/**
 * 缓存键生成器
 */
@Component
public class CacheKeyGenerator {

    private static final String TOKEN_PREFIX = "auth:token:";
    private static final String USER_PREFIX = "user:";
    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final String SMS_CODE_PREFIX = "sms:code:";
    private static final String RATE_LIMIT_PREFIX = "rate:limit:";
    private static final String PRODUCT_PREFIX = "product:";
    private static final String STOCK_PREFIX = "stock:";
    private static final String ORDER_PREFIX = "order:";
    private static final String LOCK_PREFIX = "lock:";

    /**
     * 生成用户Token缓存键
     *
     * @param token JWT令牌
     * @return 缓存键
     */
    public String getUserTokenKey(String token) {
        return TOKEN_PREFIX + token;
    }

    /**
     * 生成用户信息缓存键
     *
     * @param userId 用户ID
     * @return 缓存键
     */
    public String getUserInfoKey(Long userId) {
        return USER_PREFIX + "info:" + userId;
    }

    /**
     * 生成图形验证码缓存键
     *
     * @param captchaId 验证码ID
     * @return 缓存键
     */
    public String getCaptchaKey(String captchaId) {
        return CAPTCHA_PREFIX + captchaId;
    }

    /**
     * 生成短信验证码缓存键
     *
     * @param phone 手机号
     * @return 缓存键
     */
    public String getSmsCodeKey(String phone) {
        return SMS_CODE_PREFIX + phone;
    }

    /**
     * 生成接口限流缓存键
     *
     * @param ip        IP地址
     * @param apiPath   API路径
     * @return 缓存键
     */
    public String getRateLimitKey(String ip, String apiPath) {
        return RATE_LIMIT_PREFIX + ip + ":" + apiPath;
    }

    /**
     * 生成商品信息缓存键
     *
     * @param productId 商品ID
     * @return 缓存键
     */
    public String getProductInfoKey(Long productId) {
        return PRODUCT_PREFIX + "info:" + productId;
    }

    /**
     * 生成商品库存缓存键
     *
     * @param productId 商品ID
     * @return 缓存键
     */
    public String getProductStockKey(Long productId) {
        return STOCK_PREFIX + productId;
    }

    /**
     * 生成订单缓存键
     *
     * @param orderId 订单ID
     * @return 缓存键
     */
    public String getOrderKey(Long orderId) {
        return ORDER_PREFIX + orderId;
    }

    /**
     * 生成用户订单列表缓存键
     *
     * @param userId 用户ID
     * @return 缓存键
     */
    public String getUserOrderListKey(Long userId) {
        return USER_PREFIX + "orders:" + userId;
    }

    /**
     * 生成分布式锁键
     *
     * @param resourceName 资源名称
     * @param resourceId   资源ID
     * @return 缓存键
     */
    public String getLockKey(String resourceName, String resourceId) {
        return LOCK_PREFIX + resourceName + ":" + resourceId;
    }

    /**
     * 生成秒杀活动商品库存缓存键
     *
     * @param activityId 活动ID
     * @param productId  商品ID
     * @return 缓存键
     */
    public String getSeckillStockKey(Long activityId, Long productId) {
        return STOCK_PREFIX + "seckill:" + activityId + ":" + productId;
    }

    /**
     * 生成用户参与秒杀记录缓存键
     *
     * @param activityId 活动ID
     * @param productId  商品ID
     * @param userId     用户ID
     * @return 缓存键
     */
    public String getUserSeckillRecordKey(Long activityId, Long productId, Long userId) {
        return USER_PREFIX + "seckill:" + activityId + ":" + productId + ":" + userId;
    }
}