package com.seckill.service.impl;

import com.seckill.entity.User;
import com.seckill.exception.AuthenticationException;
import com.seckill.mapper.UserMapper;
import com.seckill.service.AuthService;
import com.seckill.util.JwtUtil;
import com.seckill.util.PasswordEncoder;
import com.seckill.util.RedisUtil;
import com.seckill.vo.request.LoginRequest;
import com.seckill.vo.request.RegisterRequest;
import com.seckill.vo.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 认证服务实现类
 */
@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisUtil redisUtil;

    private static final int MAX_LOGIN_ATTEMPTS = 5;
    private static final int LOCK_DURATION_MINUTES = 30;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long register(RegisterRequest request) {
        // 验证两次密码是否一致
        if (!Objects.equals(request.getPassword(), request.getConfirmPassword())) {
            throw AuthenticationException.passwordNotMatch();
        }

        // 验证用户名是否已存在
        if (userMapper.checkUsernameExists(request.getUsername()) > 0) {
            throw AuthenticationException.usernameExists();
        }

        // 验证手机号是否已存在
        if (userMapper.checkPhoneExists(request.getPhone()) > 0) {
            throw AuthenticationException.phoneExists();
        }

        // 验证邮箱是否已存在
        if (userMapper.checkEmailExists(request.getEmail()) > 0) {
            throw AuthenticationException.emailExists();
        }

        // 生成盐值和加密密码
        String salt = passwordEncoder.generateSalt();
        String encodedPassword = passwordEncoder.encode(request.getPassword(), salt);

        // 创建用户对象
        User user = new User()
                .setUsername(request.getUsername())
                .setPassword(encodedPassword)
                .setSalt(salt)
                .setPhone(request.getPhone())
                .setEmail(request.getEmail())
                .setNickname(request.getUsername())
                .setRole("ROLE_USER")
                .setStatus(1)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now())
                .setDeleted(0);

        // 保存用户
        userMapper.insert(user);
        return user.getId();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // 根据用户名查询用户
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw AuthenticationException.invalidCredentials();
        }

        // 检查账号状态
        checkAccountStatus(user);

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getSalt(), user.getPassword())) {
            handleLoginFailure(user);
            throw AuthenticationException.invalidCredentials();
        }

        // 更新最后登录信息
        String loginIp = getClientIp();
        userMapper.updateLastLogin(user.getId(), loginIp);

        // 生成token
        String token = jwtUtil.generateToken(user.getId());

        // 构建登录响应
        return LoginResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .token(token)
                .tokenType("Bearer")
                .expiresIn(7200000L) // 2小时
                .role(user.getRole())
                .avatar(user.getAvatar())
                .nickname(user.getNickname())
                .build();
    }

    @Override
    public void logout(String token) {
        if (token != null) {
            jwtUtil.invalidateToken(token);
        }
    }

    @Override
    public String refreshToken(String oldToken) {
        return jwtUtil.refreshToken(oldToken);
    }

    @Override
    public Long validateToken(String token) {
        if (token == null) {
            return null;
        }
        try {
            if (jwtUtil.validateToken(token)) {
                return jwtUtil.getUserIdFromToken(token);
            }
        } catch (AuthenticationException e) {
            log.debug("Token validation failed: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public Long getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String token = extractToken(request);
        return validateToken(token);
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userMapper.checkUsernameExists(username) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw AuthenticationException.accountNotFound();
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getSalt(), user.getPassword())) {
            throw AuthenticationException.oldPasswordIncorrect();
        }

        // 生成新的盐值和密码
        String newSalt = passwordEncoder.generateSalt();
        String newEncodedPassword = passwordEncoder.encode(newPassword, newSalt);

        // 更新密码
        userMapper.updatePassword(userId, newEncodedPassword, newSalt);
    }

    /**
     * 检查账号状态
     */
    private void checkAccountStatus(User user) {
        if (user.getStatus() == 0) {
            if (user.getLockEndTime() != null && user.getLockEndTime().isAfter(LocalDateTime.now())) {
                throw AuthenticationException.accountLocked();
            } else if (user.getLockEndTime() != null && user.getLockEndTime().isBefore(LocalDateTime.now())) {
                // 锁定时间已过，解锁账号
                userMapper.unlockAccount(user.getId());
            }
        }
    }

    /**
     * 处理登录失败
     */
    private void handleLoginFailure(User user) {
        userMapper.increaseLoginFailCount(user.getId());
        if (user.getLoginFailCount() + 1 >= MAX_LOGIN_ATTEMPTS) {
            userMapper.lockAccount(user.getId(), LOCK_DURATION_MINUTES);
            throw AuthenticationException.accountLocked();
        }
    }

    /**
     * 从请求中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取客户端IP
     */
    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}