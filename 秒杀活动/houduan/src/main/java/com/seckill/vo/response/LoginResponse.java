package com.seckill.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 登录响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 昵称
     */
    private String nickname;
    
    /**
     * 头像
     */
    private String avatar;
    
    /**
     * 手机号
     */
    private String phone;
    
    /**
     * 邮箱
     */
    private String email;
    
    /**
     * 认证令牌
     */
    private String token;
    
    /**
     * 令牌类型
     */
    private String tokenType;
    
    /**
     * 令牌过期时间
     */
    private Date tokenExpireTime;
    
    /**
     * 用户角色
     */
    private String[] roles;
    
    /**
     * 用户权限
     */
    private String[] permissions;
    
    /**
     * 最后登录时间
     */
    private Date lastLoginTime;
    
    /**
     * 最后登录IP
     */
    private String lastLoginIp;
}