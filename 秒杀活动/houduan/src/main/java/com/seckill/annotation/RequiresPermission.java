package com.seckill.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 标记需要特定权限才能访问的接口
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermission {
    
    /**
     * 需要的权限标识
     * 可以是多个权限，用户拥有其中任意一个权限即可访问
     */
    String[] value();
    
    /**
     * 权限验证逻辑
     * AND: 必须拥有所有权限
     * OR: 拥有任意一个权限即可
     */
    Logical logical() default Logical.OR;
    
    /**
     * 提示信息
     */
    String message() default "暂无权限";
    
    /**
     * 权限验证逻辑枚举
     */
    enum Logical {
        AND,
        OR
    }
}