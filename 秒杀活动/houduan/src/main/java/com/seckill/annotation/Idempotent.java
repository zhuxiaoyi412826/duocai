package com.seckill.annotation;

import java.lang.annotation.*;

/**
 * 幂等性注解
 * 用于防止接口重复提交
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {
    
    /**
     * 幂等性Key的前缀
     */
    String prefix() default "idempotent";
    
    /**
     * 过期时间（秒）
     * 即在过期时间内重复请求会被拦截
     */
    int expire() default 10;
    
    /**
     * 提示信息
     */
    String message() default "请勿重复提交";
    
    /**
     * 是否针对用户做幂等
     * true: 同一个用户不能重复提交
     * false: 所有用户都不能重复提交
     */
    boolean userBased() default true;
    
    /**
     * 参与幂等验证的参数名
     * 为空则使用所有参数
     */
    String[] params() default {};
}