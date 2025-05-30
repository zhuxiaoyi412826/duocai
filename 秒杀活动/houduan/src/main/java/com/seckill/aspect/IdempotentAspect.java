package com.seckill.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seckill.annotation.Idempotent;
import com.seckill.exception.BusinessException;
import com.seckill.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 幂等性切面
 */
@Slf4j
@Aspect
@Component
public class IdempotentAspect {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @Resource
    private ObjectMapper objectMapper;

    @Pointcut("@annotation(com.seckill.annotation.Idempotent)")
    public void idempotent() {
    }

    @Around("idempotent()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        
        // 获取注解信息
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return point.proceed();
        }

        // 构建幂等性键
        String key = buildIdempotentKey(request, point, idempotent, method);
        
        // 尝试获取锁
        boolean locked = lock(key, idempotent.expire());
        if (!locked) {
            log.warn("重复请求被拦截，key={}", key);
            throw new BusinessException(idempotent.message());
        }

        try {
            // 执行原方法
            return point.proceed();
        } finally {
            // 方法执行完后删除锁
            unlock(key);
        }
    }

    /**
     * 构建幂等性键
     */
    private String buildIdempotentKey(HttpServletRequest request, ProceedingJoinPoint point, 
                                    Idempotent idempotent, Method method) throws Exception {
        StringBuilder key = new StringBuilder("idempotent:");
        
        // 添加前缀
        key.append(idempotent.prefix()).append(":");
        
        // 添加类名和方法名
        key.append(method.getDeclaringClass().getSimpleName())
           .append(":")
           .append(method.getName())
           .append(":");

        // 如果是基于用户的幂等性检查，添加用户标识
        if (idempotent.userBased()) {
            String userId = request.getHeader("X-User-Id");
            if (userId == null) {
                throw new BusinessException("用户未登录");
            }
            key.append(userId).append(":");
        }

        // 获取参与幂等验证的参数
        String[] parameterNames = signature.getParameterNames();
        Object[] args = point.getArgs();
        Map<String, Object> paramMap = new HashMap<>();
        
        // 如果指定了参与幂等验证的参数
        if (idempotent.params().length > 0) {
            for (String param : idempotent.params()) {
                for (int i = 0; i < parameterNames.length; i++) {
                    if (param.equals(parameterNames[i])) {
                        paramMap.put(param, args[i]);
                        break;
                    }
                }
            }
        } else {
            // 使用所有参数
            for (int i = 0; i < parameterNames.length; i++) {
                if (args[i] != null) {
                    paramMap.put(parameterNames[i], args[i]);
                }
            }
        }

        // 将参数转换为字符串并添加到key中
        if (!paramMap.isEmpty()) {
            key.append(objectMapper.writeValueAsString(paramMap));
        }

        return CommonUtil.md5(key.toString());
    }

    /**
     * 获取锁
     */
    private boolean lock(String key, int expire) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue()
                .setIfAbsent(key, "1", expire, TimeUnit.SECONDS));
    }

    /**
     * 释放锁
     */
    private void unlock(String key) {
        redisTemplate.delete(key);
    }
}