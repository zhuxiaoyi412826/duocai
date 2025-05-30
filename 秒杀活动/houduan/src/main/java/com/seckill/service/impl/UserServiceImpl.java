package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.common.Constants;
import com.seckill.entity.User;
import com.seckill.exception.AuthException;
import com.seckill.exception.BusinessException;
import com.seckill.mapper.UserMapper;
import com.seckill.service.UserService;
import com.seckill.util.JwtUtil;
import com.seckill.vo.request.LoginRequest;
import com.seckill.vo.request.RegisterRequest;
import com.seckill.vo.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String register(RegisterRequest request) {
        // 验证密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 验证用户名是否可用
        if (!checkUsernameAvailable(request.getUsername())) {
            throw new BusinessException("用户名已被使用");
        }

        // 验证手机号是否可用
        if (request.getPhone() != null && !checkPhoneAvailable(request.getPhone())) {
            throw new BusinessException("手机号已被使用");
        }

        // 验证邮箱是否可用
        if (request.getEmail() != null && !checkEmailAvailable(request.getEmail())) {
            throw new BusinessException("邮箱已被使用");
        }

        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(request.getUsername());
        user.setStatus(1);
        user.setRoles("ROLE_USER");
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDeleted(0);

        // 保存用户
        save(user);
        
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询用户
        User user = getByUsername(request.getUsername());
        if (user == null) {
            throw AuthException.invalidCredentials();
        }

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw AuthException.invalidCredentials();
        }

        // 验证用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 生成token
        String token = jwtUtil.generateToken(user.getId());
        Date expireTime = jwtUtil.getExpirationDateFromToken(token);

        // 更新登录信息
        updateLoginInfo(user.getId(), request.getRememberMe() ? "记住登录" : "普通登录");

        // 构建登录响应
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .phone(user.getPhone())
                .email(user.getEmail())
                .token(token)
                .tokenType("Bearer")
                .tokenExpireTime(expireTime)
                .roles(getRoles(user.getId()))
                .permissions(getPermissions(user.getId()))
                .lastLoginTime(user.getLastLoginTime())
                .lastLoginIp(user.getLastLoginIp())
                .build();
    }

    @Override
    public User getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .eq(User::getIsDeleted, 0));
    }

    @Override
    public User getByPhone(String phone) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getPhone, phone)
                .eq(User::getIsDeleted, 0));
    }

    @Override
    public User getByEmail(String email) {
        return getOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email)
                .eq(User::getIsDeleted, 0));
    }

    @Override
    public void updateLoginInfo(String userId, String ip) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(ip);
        updateById(user);
    }

    @Override
    public boolean checkUsernameAvailable(String username) {
        return getByUsername(username) == null;
    }

    @Override
    public boolean checkPhoneAvailable(String phone) {
        return getByPhone(phone) == null;
    }

    @Override
    public boolean checkEmailAvailable(String email) {
        return getByEmail(email) == null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(String userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setPassword(passwordEncoder.encode(newPassword));
        updateUser.setUpdateTime(new Date());
        updateById(updateUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String userId, String password) {
        User user = new User();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(password));
        user.setUpdateTime(new Date());
        updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String userId, Integer status) {
        User user = new User();
        user.setId(userId);
        user.setStatus(status);
        user.setUpdateTime(new Date());
        updateById(user);
    }

    @Override
    public String[] getRoles(String userId) {
        User user = getById(userId);
        if (user == null || user.getRoles() == null) {
            return new String[0];
        }
        return user.getRoles().split(",");
    }

    @Override
    public String[] getPermissions(String userId) {
        // 这里简单实现，实际应该根据角色查询对应的权限
        String[] roles = getRoles(userId);
        if (roles.length == 0) {
            return new String[0];
        }
        
        if (Arrays.asList(roles).contains("ROLE_ADMIN")) {
            return new String[]{"*"};
        }
        
        return new String[]{"user:view", "user:edit"};
    }
}