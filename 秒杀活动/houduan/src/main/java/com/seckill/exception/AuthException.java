package com.seckill.exception;

import com.seckill.common.Constants;

/**
 * 认证异常类
 */
public class AuthException extends BusinessException {
    
    public AuthException(String message) {
        super(Constants.UNAUTHORIZED_CODE, message);
    }
    
    public AuthException(String message, Throwable cause) {
        super(Constants.UNAUTHORIZED_CODE, message, cause);
    }
    
    /**
     * 用户未登录异常
     */
    public static AuthException notLogin() {
        return new AuthException(Constants.UNAUTHORIZED_MESSAGE);
    }
    
    /**
     * 登录失败异常
     */
    public static AuthException loginFailed(String message) {
        return new AuthException(message);
    }
    
    /**
     * 令牌过期异常
     */
    public static AuthException tokenExpired() {
        return new AuthException("登录已过期，请重新登录");
    }
    
    /**
     * 令牌无效异常
     */
    public static AuthException invalidToken() {
        return new AuthException("无效的认证令牌");
    }
    
    /**
     * 账号被锁定异常
     */
    public static AuthException accountLocked(int minutes) {
        return new AuthException("账号已被锁定，请" + minutes + "分钟后再试");
    }
    
    /**
     * 账号或密码错误异常
     */
    public static AuthException invalidCredentials() {
        return new AuthException("账号或密码错误");
    }
    
    /**
     * 账号已在其他设备登录异常
     */
    public static AuthException accountLoginElsewhere() {
        return new AuthException("账号已在其他设备登录");
    }
}