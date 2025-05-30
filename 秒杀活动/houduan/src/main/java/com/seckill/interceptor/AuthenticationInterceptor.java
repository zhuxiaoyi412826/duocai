package com.seckill.interceptor;

import com.seckill.annotation.RequiresLogin;
import com.seckill.annotation.RequiresPermission;
import com.seckill.exception.AccessDeniedException;
import com.seckill.exception.AuthenticationException;
import com.seckill.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.seckill.util.UserContext;

/**
 * 认证授权拦截器
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 如果不是方法处理器，直接放行
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        
        // 获取类级别的注解
        RequiresLogin classLogin = method.getDeclaringClass().getAnnotation(RequiresLogin.class);
        RequiresPermission classPermission = method.getDeclaringClass().getAnnotation(RequiresPermission.class);
        
        // 获取方法级别的注解
        RequiresLogin methodLogin = method.getAnnotation(RequiresLogin.class);
        RequiresPermission methodPermission = method.getAnnotation(RequiresPermission.class);

        // 如果没有任何认证和权限注解，直接放行
        if (classLogin == null && classPermission == null && methodLogin == null && methodPermission == null) {
            return true;
        }

        // 获取token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            throw new AuthenticationException("未提供token");
        }

        // 验证token并获取claims
        Claims claims = verifyToken(token);
        
        // 获取用户信息
        String userId = claims.getSubject();
        
        // 将用户ID放入请求头，方便后续使用
        request.setAttribute("userId", userId);

        // 如果需要登录认证
        if (classLogin != null || methodLogin != null) {
            RequiresLogin loginAnnotation = methodLogin != null ? methodLogin : classLogin;
            checkLogin(userId, loginAnnotation);
        }

        // 如果需要权限验证
        if (classPermission != null || methodPermission != null) {
            RequiresPermission permissionAnnotation = methodPermission != null ? methodPermission : classPermission;
            checkPermission(userId, permissionAnnotation);
        }

        return true;
    }

    /**
     * 从请求头中获取token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(TOKEN_HEADER);
        if (token != null && token.startsWith(TOKEN_PREFIX)) {
            return token.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    /**
     * 验证token
     */
    private Claims verifyToken(String token) {
        try {
            return jwtUtil.parseToken(token);
        } catch (Exception e) {
            log.warn("Token验证失败：{}", e.getMessage());
            throw new AuthenticationException("无效的token");
        }
    }

    /**
     * 检查登录状态
     */
    private void checkLogin(String userId, RequiresLogin annotation) {
        // 从Redis中获取用户登录状态
        String key = "user:login:" + userId;
        Boolean isLogin = redisTemplate.hasKey(key);
        
        if (!Boolean.TRUE.equals(isLogin)) {
            throw new AuthenticationException(annotation.message());
        }

        // 如果需要刷新token
        if (annotation.refreshToken()) {
            redisTemplate.expire(key, jwtUtil.getExpiration());
        }
    }

    /**
     * 检查权限
     */
    @SuppressWarnings("unchecked")
    private void checkPermission(String userId, RequiresPermission annotation) {
        // 从Redis中获取用户权限列表
        String key = "user:permissions:" + userId;
        Set<String> userPermissions = (Set<String>) redisTemplate.opsForValue().get(key);
        
        if (userPermissions == null || userPermissions.isEmpty()) {
            throw new AccessDeniedException(annotation.message());
        }

        String[] requiredPermissions = annotation.value();
        boolean hasPermission = false;

        // 根据验证逻辑检查权限
        if (annotation.logical() == RequiresPermission.Logical.AND) {
            // 必须拥有所有权限
            hasPermission = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
        } else {
            // 拥有任意一个权限即可
            hasPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
        }

        if (!hasPermission) {
            throw new AccessDeniedException(annotation.message());
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理用户上下文，防止内存泄漏
        UserContext.clear();
    }
}