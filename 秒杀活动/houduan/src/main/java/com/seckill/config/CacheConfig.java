package com.seckill.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * 缓存配置类
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * 配置默认的本地缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterAccess(60, TimeUnit.MINUTES)
                // 初始的缓存空间大小
                .initialCapacity(100)
                // 缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }

    /**
     * 配置商品信息专用缓存管理器
     */
    @Bean
    public CacheManager productCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入后经过固定时间过期
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 设置缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }

    /**
     * 配置库存信息专用缓存管理器
     */
    @Bean
    public CacheManager stockCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.SECONDS)
                // 设置缓存的最大条数
                .maximumSize(200));
        return cacheManager;
    }

    /**
     * 配置用户信息专用缓存管理器
     */
    @Bean
    public CacheManager userCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次访问后经过固定时间过期
                .expireAfterAccess(30, TimeUnit.MINUTES)
                // 设置缓存的最大条数
                .maximumSize(500));
        return cacheManager;
    }

    /**
     * 配置验证码专用缓存管理器
     */
    @Bean
    public CacheManager captchaCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最后一次写入后经过固定时间过期
                .expireAfterWrite(5, TimeUnit.MINUTES)
                // 设置缓存的最大条数
                .maximumSize(1000));
        return cacheManager;
    }

    /**
     * 配置热点数据缓存管理器
     */
    @Bean
    public CacheManager hotDataCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 根据权重进行缓存驱逐
                .weigher((key, value) -> 1)
                // 设置最大权重
                .maximumWeight(100000)
                // 设置更新后经过固定时间过期
                .expireAfterWrite(1, TimeUnit.MINUTES)
                // 设置刷新后经过固定时间过期
                .refreshAfterWrite(30, TimeUnit.SECONDS));
        return cacheManager;
    }

    /**
     * 配置限流器缓存管理器
     */
    @Bean
    public CacheManager rateLimiterCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置写入后经过固定时间过期
                .expireAfterWrite(1, TimeUnit.SECONDS)
                // 设置最大条数
                .maximumSize(10000));
        return cacheManager;
    }
}