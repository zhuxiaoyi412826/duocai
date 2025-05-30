package com.seckill.exception;

import com.seckill.common.ResultCode;

/**
 * 认证异常类
 */
public class AuthenticationException extends BusinessException {

    public AuthenticationException(String message) {
        super(ResultCode.UNAUTHORIZED, message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(ResultCode.UNAUTHORIZED, message, cause);
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("用户名或密码错误");
    }

    public static AuthenticationException invalidToken() {
        return new AuthenticationException("无效的认证令牌");
    }

    public static AuthenticationException tokenExpired() {
        return new AuthenticationException("认证令牌已过期");
    }

    public static AuthenticationException accountLocked() {
        return new AuthenticationException("账号已被锁定");
    }

    public static AuthenticationException accountDisabled() {
        return new AuthenticationException("账号已被禁用");
    }

    public static AuthenticationException unauthorized() {
        return new AuthenticationException("未经授权的访问");
    }

    public static AuthenticationException invalidCaptcha() {
        return new AuthenticationException("验证码错误或已过期");
    }

    public static AuthenticationException invalidSmsCode() {
        return new AuthenticationException("短信验证码错误或已过期");
    }

    public static AuthenticationException tooManyRequests() {
        return new AuthenticationException("请求过于频繁，请稍后再试");
    }

    public static AuthenticationException usernameExists() {
        return new AuthenticationException("用户名已存在");
    }

    public static AuthenticationException phoneExists() {
        return new AuthenticationException("手机号已被注册");
    }

    public static AuthenticationException emailExists() {
        return new AuthenticationException("邮箱已被注册");
    }

    public static AuthenticationException passwordNotMatch() {
        return new AuthenticationException("两次输入的密码不一致");
    }

    public static AuthenticationException oldPasswordIncorrect() {
        return new AuthenticationException("原密码错误");
    }

    public static AuthenticationException accountNotFound() {
        return new AuthenticationException("账号不存在");
    }

    public static AuthenticationException sessionExpired() {
        return new AuthenticationException("会话已过期，请重新登录");
    }
}