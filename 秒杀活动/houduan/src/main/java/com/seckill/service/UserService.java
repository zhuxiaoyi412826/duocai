package com.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.seckill.entity.User;
import com.seckill.vo.request.LoginRequest;
import com.seckill.vo.request.RegisterRequest;
import com.seckill.vo.response.LoginResponse;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 注册成功的用户ID
     */
    String register(RegisterRequest request);
    
    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应信息
     */
    LoginResponse login(LoginRequest request);
    
    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    User getByUsername(String username);
    
    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    User getByPhone(String phone);
    
    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    User getByEmail(String email);
    
    /**
     * 更新用户登录信息
     *
     * @param userId 用户ID
     * @param ip     登录IP
     */
    void updateLoginInfo(String userId, String ip);
    
    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return true-可用，false-不可用
     */
    boolean checkUsernameAvailable(String username);
    
    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return true-可用，false-不可用
     */
    boolean checkPhoneAvailable(String phone);
    
    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return true-可用，false-不可用
     */
    boolean checkEmailAvailable(String email);
    
    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void updatePassword(String userId, String oldPassword, String newPassword);
    
    /**
     * 重置密码
     *
     * @param userId   用户ID
     * @param password 新密码
     */
    void resetPassword(String userId, String password);
    
    /**
     * 修改用户状态
     *
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-正常）
     */
    void updateStatus(String userId, Integer status);
    
    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    String[] getRoles(String userId);
    
    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    String[] getPermissions(String userId);
}