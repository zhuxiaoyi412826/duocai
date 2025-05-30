package com.seckill.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis工具类
 */
@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 分布式锁的Lua脚本
    private static final String LOCK_SCRIPT = 
        "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
        "  redis.call('expire', KEYS[1], ARGV[2]) " +
        "  return 1 " +
        "else " +
        "  return 0 " +
        "end";

    private static final String UNLOCK_SCRIPT = 
        "if redis.call('get', KEYS[1]) == ARGV[1] then " +
        "  return redis.call('del', KEYS[1]) " +
        "else " +
        "  return 0 " +
        "end";

    // 库存扣减的Lua脚本
    private static final String STOCK_DEDUCT_SCRIPT = 
        "if redis.call('exists', KEYS[1]) == 1 then " +
        "  local stock = tonumber(redis.call('get', KEYS[1])) " +
        "  if stock >= tonumber(ARGV[1]) then " +
        "    redis.call('decrby', KEYS[1], ARGV[1]) " +
        "    return 1 " +
        "  end " +
        "end " +
        "return 0";

    /**
     * 设置缓存
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            log.error("Redis set error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置缓存并设置过期时间
     */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis set with timeout error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取缓存
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Redis get error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 删除缓存
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("Redis delete error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 批量删除缓存
     */
    public void delete(List<String> keys) {
        try {
            redisTemplate.delete(keys);
        } catch (Exception e) {
            log.error("Redis batch delete error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 判断key是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Redis hasKey error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, TimeUnit.SECONDS));
        } catch (Exception e) {
            log.error("Redis expire error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 获取过期时间
     */
    public long getExpire(String key) {
        try {
            return redisTemplate.getExpire(key, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Redis getExpire error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 递增
     */
    public long incr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().increment(key, delta);
        } catch (Exception e) {
            log.error("Redis incr error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 递减
     */
    public long decr(String key, long delta) {
        try {
            return redisTemplate.opsForValue().decrement(key, delta);
        } catch (Exception e) {
            log.error("Redis decr error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Hash操作 - 设置字段值
     */
    public void hset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception e) {
            log.error("Redis hset error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Hash操作 - 获取字段值
     */
    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception e) {
            log.error("Redis hget error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Hash操作 - 删除字段
     */
    public void hdel(String key, Object... fields) {
        try {
            redisTemplate.opsForHash().delete(key, fields);
        } catch (Exception e) {
            log.error("Redis hdel error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Hash操作 - 获取所有字段值
     */
    public Map<Object, Object> hgetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception e) {
            log.error("Redis hgetAll error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Set操作 - 添加元素
     */
    public void sadd(String key, Object... values) {
        try {
            redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("Redis sadd error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * Set操作 - 获取所有元素
     */
    public Set<Object> smembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis smembers error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 尝试获取分布式锁
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        try {
            RedisScript<Long> script = new DefaultRedisScript<>(LOCK_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script, 
                Collections.singletonList(lockKey), 
                requestId, 
                String.valueOf(expireTime));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("Redis tryLock error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 释放分布式锁
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            RedisScript<Long> script = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script, 
                Collections.singletonList(lockKey), 
                requestId);
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("Redis releaseLock error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }

    /**
     * 扣减库存
     */
    public boolean deductStock(String key, int amount) {
        try {
            RedisScript<Long> script = new DefaultRedisScript<>(STOCK_DEDUCT_SCRIPT, Long.class);
            Long result = redisTemplate.execute(script, 
                Collections.singletonList(key), 
                String.valueOf(amount));
            return result != null && result == 1;
        } catch (Exception e) {
            log.error("Redis deductStock error: ", e);
            throw new RuntimeException("Redis操作失败", e);
        }
    }
}