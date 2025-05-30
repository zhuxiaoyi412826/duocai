package com.seckill.interceptor;

import com.seckill.annotation.AccessLimit;
import com.seckill.common.Constants;
import com.seckill.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * 访问限制拦截器
 */
@Slf4j
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        // 获取方法上的AccessLimit注解
        AccessLimit accessLimit = method.getAnnotation(AccessLimit.class);
        if (accessLimit == null) {
            return true;
        }

        // 获取注解中的参数
        int seconds = accessLimit.seconds();
        int maxCount = accessLimit.maxCount();
        boolean needLogin = accessLimit.needLogin();

        String key = request.getRequestURI();

        // 如果需要登录，获取用户ID
        if (needLogin) {
            String userId = (String) request.getAttribute(Constants.CURRENT_USER_ID);
            if (userId == null) {
                throw new BusinessException("请先登录");
            }
            key = key + ":" + userId;
        } else {
            // 如果不需要登录，使用IP地址作为key
            String ip = getIpAddress(request);
            key = key + ":" + ip;
        }

        // 构建Redis key
        String redisKey = String.format(Constants.REDIS_ACCESS_LIMIT_KEY, key);

        try {
            // 获取当前访问次数
            Integer count = (Integer) redisTemplate.opsForValue().get(redisKey);
            
            if (count == null) {
                // 第一次访问，设置初始值和过期时间
                redisTemplate.opsForValue().set(redisKey, 1, seconds, TimeUnit.SECONDS);
            } else if (count < maxCount) {
                // 访问次数未达到上限，递增访问次数
                redisTemplate.opsForValue().increment(redisKey);
            } else {
                // 访问次数达到上限，抛出异常
                throw new BusinessException("访问太频繁，请稍后再试");
            }

            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("访问限制校验失败", e);
            return true;
        }
    }

    /**
     * 获取请求的真实IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多级代理，取第一个IP地址
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}