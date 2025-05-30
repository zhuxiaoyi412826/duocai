package com.seckill.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的分布式限流器
 */
@Slf4j
@Component
public class RateLimiter {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 限流脚本
     * 原理：
     * 1. 获取当前时间窗口的请求数
     * 2. 如果请求数小于限流阈值，则请求数+1，并设置过期时间
     * 3. 如果请求数大于等于限流阈值，则拒绝请求
     */
    private static final String RATE_LIMITER_SCRIPT = 
            "local key = KEYS[1] " +
            "local limit = tonumber(ARGV[1]) " +
            "local expire = tonumber(ARGV[2]) " +
            "local current = tonumber(redis.call('get', key) or '0') " +
            "if current + 1 > limit then " +
            "    return 0 " +
            "else " +
            "    redis.call('incrby', key, 1) " +
            "    redis.call('expire', key, expire) " +
            "    return 1 " +
            "end";

    /**
     * 尝试获取令牌
     *
     * @param key    限流key
     * @param limit  限流阈值
     * @param expire 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryAcquire(String key, int limit, int expire) {
        try {
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptText(RATE_LIMITER_SCRIPT);
            redisScript.setResultType(Long.class);
            
            Long result = redisTemplate.execute(
                    redisScript,
                    Collections.singletonList(key),
                    limit,
                    expire
            );
            
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("限流器异常：", e);
            // 如果Redis出现异常，默认放行
            return true;
        }
    }

    /**
     * 针对IP的限流
     *
     * @param ip     IP地址
     * @param limit  限流阈值
     * @param expire 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryAcquireByIp(String ip, String action, int limit, int expire) {
        String key = "rate_limit:ip:" + ip + ":" + action;
        return tryAcquire(key, limit, expire);
    }

    /**
     * 针对用户的限流
     *
     * @param userId 用户ID
     * @param action 操作类型
     * @param limit  限流阈值
     * @param expire 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryAcquireByUser(Long userId, String action, int limit, int expire) {
        String key = "rate_limit:user:" + userId + ":" + action;
        return tryAcquire(key, limit, expire);
    }

    /**
     * 针对接口的限流
     *
     * @param uri    接口URI
     * @param limit  限流阈值
     * @param expire 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryAcquireByUri(String uri, int limit, int expire) {
        String key = "rate_limit:uri:" + uri;
        return tryAcquire(key, limit, expire);
    }

    /**
     * 获取当前请求数
     *
     * @param key 限流key
     * @return 当前请求数
     */
    public int getCurrentRequests(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? 0 : Integer.parseInt(value.toString());
    }

    /**
     * 重置计数器
     *
     * @param key 限流key
     */
    public void reset(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 设置过期时间
     *
     * @param key    限流key
     * @param expire 过期时间（秒）
     */
    public void expire(String key, int expire) {
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
    }
}