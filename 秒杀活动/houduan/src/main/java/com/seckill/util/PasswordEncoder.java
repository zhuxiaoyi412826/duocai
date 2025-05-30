package com.seckill.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * 密码加密工具类
 */
@Component
public class PasswordEncoder {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public PasswordEncoder() {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * 生成随机盐值
     *
     * @return 盐值字符串
     */
    public String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * 加密密码
     *
     * @param rawPassword 原始密码
     * @param salt        盐值
     * @return 加密后的密码
     */
    public String encode(String rawPassword, String salt) {
        return bCryptPasswordEncoder.encode(rawPassword + salt);
    }

    /**
     * 验证密码
     *
     * @param rawPassword     原始密码
     * @param salt            盐值
     * @param encodedPassword 加密后的密码
     * @return 验证成功返回true，失败返回false
     */
    public boolean matches(String rawPassword, String salt, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword + salt, encodedPassword);
    }

    /**
     * 不使用盐值直接加密密码（用于临时场景）
     *
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    public String encodeWithoutSalt(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
     * 不使用盐值直接验证密码（用于临时场景）
     *
     * @param rawPassword     原始密码
     * @param encodedPassword 加密后的密码
     * @return 验证成功返回true，失败返回false
     */
    public boolean matchesWithoutSalt(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
    }
}