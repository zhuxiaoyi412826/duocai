package com.seckill.aspect;

import com.seckill.annotation.RateLimit;
import com.seckill.common.Result;
import com.seckill.exception.BusinessException;
import com.seckill.util.IpUtil;
import com.seckill.util.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * 限流注解切面
 */
@Slf4j
@Aspect
@Component
public class RateLimitAspect {

    @Resource
    private RateLimiter rateLimiter;

    @Pointcut("@annotation(com.seckill.annotation.RateLimit)")
    public void rateLimit() {
    }

    @Around("rateLimit()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        // 获取注解信息
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);
        if (rateLimit == null) {
            return point.proceed();
        }

        // 根据限流类型获取限流key
        String key = generateKey(request, rateLimit, method);
        
        // 尝试获取令牌
        boolean acquired = rateLimiter.tryAcquire(key, rateLimit.limit(), rateLimit.expire());
        if (!acquired) {
            log.warn("请求被限流，key={}, limit={}, expire={}", key, rateLimit.limit(), rateLimit.expire());
            throw new BusinessException(429, rateLimit.message());
        }

        // 继续执行原方法
        return point.proceed();
    }

    /**
     * 根据限流类型生成限流key
     */
    private String generateKey(HttpServletRequest request, RateLimit rateLimit, Method method) {
        StringBuilder key = new StringBuilder("rate_limit:");
        
        // 添加前缀
        if (!rateLimit.prefix().isEmpty()) {
            key.append(rateLimit.prefix()).append(":");
        }

        // 根据限流类型生成key
        switch (rateLimit.type()) {
            case IP:
                key.append("ip:").append(IpUtil.getIpAddr(request));
                break;
            case USER:
                // 这里假设用户ID存储在请求头的X-User-Id中
                String userId = request.getHeader("X-User-Id");
                if (userId == null) {
                    throw new BusinessException("用户未登录");
                }
                key.append("user:").append(userId);
                break;
            case URI:
                key.append("uri:").append(request.getRequestURI());
                break;
            default:
                key.append(method.getDeclaringClass().getName())
                   .append(":")
                   .append(method.getName());
        }

        return key.toString();
    }
}