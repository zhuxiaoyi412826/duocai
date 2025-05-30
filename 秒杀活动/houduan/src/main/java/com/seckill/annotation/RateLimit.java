package com.seckill.annotation;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {
    
    /**
     * 限流类型
     */
    Type type() default Type.IP;
    
    /**
     * 限流key前缀
     */
    String prefix() default "";
    
    /**
     * 时间窗口内最大请求数
     */
    int limit() default 100;
    
    /**
     * 时间窗口（秒）
     */
    int expire() default 60;
    
    /**
     * 限流提示语
     */
    String message() default "访问太频繁，请稍后再试";

    /**
     * 限流类型枚举
     */
    enum Type {
        /**
         * 根据IP限流
         */
        IP,
        
        /**
         * 根据用户限流
         */
        USER,
        
        /**
         * 根据接口限流
         */
        URI
    }
}