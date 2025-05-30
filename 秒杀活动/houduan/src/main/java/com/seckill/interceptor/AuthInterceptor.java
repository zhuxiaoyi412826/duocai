package com.seckill.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seckill.common.Result;
import com.seckill.common.ResultCode;
import com.seckill.exception.AuthenticationException;
import com.seckill.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 认证拦截器
 */
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private final PathMatcher pathMatcher = new AntPathMatcher();

    // 不需要认证的路径
    private final List<String> excludePaths = Arrays.asList(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/check-username",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/v2/api-docs",
            "/webjars/**",
            "/error"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 检查是否是不需要认证的路径
        String requestPath = request.getRequestURI();
        if (isExcludePath(requestPath)) {
            return true;
        }

        // 获取token
        String token = extractToken(request);
        if (token == null) {
            handleAuthenticationError(response, "未提供认证令牌");
            return false;
        }

        try {
            // 验证token
            Long userId = authService.validateToken(token);
            if (userId == null) {
                handleAuthenticationError(response, "无效的认证令牌");
                return false;
            }

            // 将用户ID存入请求属性中，供后续使用
            request.setAttribute("userId", userId);
            return true;
        } catch (AuthenticationException e) {
            handleAuthenticationError(response, e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Authentication error", e);
            handleAuthenticationError(response, "认证过程发生错误");
            return false;
        }
    }

    /**
     * 检查是否是不需要认证的路径
     */
    private boolean isExcludePath(String requestPath) {
        return excludePaths.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestPath));
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 处理认证错误
     */
    private void handleAuthenticationError(HttpServletResponse response, String message) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        Result<?> result = Result.error(ResultCode.UNAUTHORIZED, message);
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}