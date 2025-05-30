package com.seckill.annotation;

import java.lang.annotation.*;

/**
 * 登录认证注解
 * 标记需要登录才能访问的接口
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresLogin {
    
    /**
     * 是否需要刷新token
     */
    boolean refreshToken() default true;
    
    /**
     * 提示信息
     */
    String message() default "请先登录";
}