package com.seckill.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 访问限制注解
 * 用于限制接口的访问频率
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessLimit {
    
    /**
     * 限制时间，单位：秒
     */
    int seconds() default 60;
    
    /**
     * 允许访问最大次数
     */
    int maxCount() default 5;
    
    /**
     * 是否需要登录
     */
    boolean needLogin() default true;
}