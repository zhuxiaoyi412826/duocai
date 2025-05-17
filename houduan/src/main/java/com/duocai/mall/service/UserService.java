package com.duocai.mall.service;

import com.duocai.mall.model.Users;

/**
 * 用户服务接口
 * @author trae
 */
public interface UserService {
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册成功的用户信息
     */
    Users register(Users user);
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return JWT token
     */
    String login(String username, String password);
    
    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    Users getUserById(Long id);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    Users updateUser(Users user);
    
    /**
     * 修改密码
     * @param id 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(Long id, String oldPassword, String newPassword);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);
}