package com.seckill.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置属性类
 * 映射application.yml中的app前缀配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    /**
     * JWT配置
     */
    private JwtProperties jwt = new JwtProperties();

    /**
     * 业务配置
     */
    private BusinessProperties business = new BusinessProperties();

    /**
     * 缓存配置
     */
    private CacheProperties cache = new CacheProperties();

    /**
     * JWT配置属性
     */
    @Data
    public static class JwtProperties {
        /**
         * JWT加密密钥
         */
        private String secretKey = "default-secret-key";

        /**
         * Token过期时间（毫秒）
         */
        private long expiration = 604800000; // 默认7天

        /**
         * Token中用户ID的键名
         */
        private String userIdKey = "userId";
    }

    /**
     * 业务配置属性
     */
    @Data
    public static class BusinessProperties {
        /**
         * 秒杀相关配置
         */
        private SeckillProperties seckill = new SeckillProperties();

        /**
         * 订单相关配置
         */
        private OrderProperties order = new OrderProperties();

        /**
         * 秒杀配置属性
         */
        @Data
        public static class SeckillProperties {
            /**
             * 商品库存预热阈值
             */
            private int stockWarmUpThreshold = 100;

            /**
             * 用户限购数量
             */
            private int userBuyLimit = 1;
        }

        /**
         * 订单配置属性
         */
        @Data
        public static class OrderProperties {
            /**
             * 订单超时时间（分钟）
             */
            private int timeout = 30;

            /**
             * 订单自动取消任务执行间隔（秒）
             */
            private int cancelTaskInterval = 60;
        }
    }

    /**
     * 缓存配置属性
     */
    @Data
    public static class CacheProperties {
        /**
         * 默认过期时间（秒）
         */
        private int defaultExpire = 7200; // 默认2小时

        /**
         * 空值过期时间（秒）
         */
        private int nullExpire = 300; // 默认5分钟
    }
}