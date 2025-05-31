package com.seckill.exception;

/**
 * 认证异常类
 */
public class AuthException extends BusinessException {

    public AuthException(String message) {
        super(message);
    }

    public AuthException(int code, String message) {
        super(code, message);
    }

    /**
     * 未认证异常
     */
    public static AuthException unauthorized() {
        return new AuthException(401, "未认证，请先登录");
    }

    /**
     * 无效凭证异常
     */
    public static AuthException invalidCredentials() {
        return new AuthException(401, "用户名或密码错误");
    }

    /**
     * 无权限异常
     */
    public static AuthException forbidden() {
        return new AuthException(403, "无权限访问");
    }

    /**
     * Token过期异常
     */
    public static AuthException tokenExpired() {
        return new AuthException(401, "Token已过期");
    }

    /**
     * Token无效异常
     */
    public static AuthException tokenInvalid() {
        return new AuthException(401, "Token无效");
    }

    /**
     * 账号被禁用异常
     */
    public static AuthException accountDisabled() {
        return new AuthException(403, "账号已被禁用");
    }
}