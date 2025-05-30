package com.seckill.interceptor;

import com.seckill.common.Constants;
import com.seckill.entity.User;
import com.seckill.exception.AuthException;
import com.seckill.service.UserService;
import com.seckill.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * 登录拦截器
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取请求头中的token
        String authorization = request.getHeader(jwtUtil.getHeader());
        String token = jwtUtil.getTokenFromHeader(authorization);

        // 如果token为空，抛出未认证异常
        if (!StringUtils.hasText(token)) {
            throw AuthException.unauthorized();
        }

        try {
            // 从token中获取用户ID
            String userId = jwtUtil.getUserIdFromToken(token);
            if (!StringUtils.hasText(userId)) {
                throw AuthException.unauthorized();
            }

            // 从Redis中获取用户信息
            String redisKey = String.format(Constants.REDIS_USER_KEY, userId);
            User user = (User) redisTemplate.opsForValue().get(redisKey);

            // 如果Redis中没有用户信息，从数据库查询
            if (user == null) {
                user = userService.getById(userId);
                if (user == null) {
                    throw AuthException.unauthorized();
                }

                // 将用户信息存入Redis，设置过期时间
                redisTemplate.opsForValue().set(redisKey, user, 24, TimeUnit.HOURS);
            }

            // 验证token是否有效
            if (!jwtUtil.validateToken(token, userId)) {
                throw AuthException.unauthorized();
            }

            // 如果token即将过期，刷新token
            if (jwtUtil.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis() < Constants.TOKEN_REFRESH_INTERVAL) {
                String newToken = jwtUtil.refreshToken(token);
                response.setHeader(jwtUtil.getHeader(), "Bearer " + newToken);
            }

            // 将用户信息存入request属性中，方便后续使用
            request.setAttribute(Constants.CURRENT_USER_ID, userId);
            request.setAttribute(Constants.CURRENT_USER, user);

            return true;
        } catch (Exception e) {
            log.error("验证token失败", e);
            throw AuthException.unauthorized();
        }
    }
}