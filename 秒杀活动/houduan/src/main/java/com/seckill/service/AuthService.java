package com.seckill.service;

import com.seckill.vo.request.LoginRequest;
import com.seckill.vo.request.RegisterRequest;
import com.seckill.vo.response.LoginResponse;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功返回用户ID
     */
    Long register(RegisterRequest request);

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应，包含token等信息
     */
    LoginResponse login(LoginRequest request);

    /**
     * 退出登录
     *
     * @param token 用户token
     */
    void logout(String token);

    /**
     * 刷新token
     *
     * @param oldToken 旧token
     * @return 新token
     */
    String refreshToken(String oldToken);

    /**
     * 验证token
     *
     * @param token 待验证的token
     * @return 如果token有效，返回用户ID；否则返回null
     */
    Long validateToken(String token);

    /**
     * 获取当前登录用户ID
     *
     * @return 当前登录用户ID，如果未登录返回null
     */
    Long getCurrentUserId();

    /**
     * 检查用户名是否已存在
     *
     * @param username 用户名
     * @return true表示已存在，false表示不存在
     */
    boolean isUsernameExists(String username);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}