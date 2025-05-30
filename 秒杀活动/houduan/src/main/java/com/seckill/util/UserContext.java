package com.seckill.util;

import lombok.extern.slf4j.Slf4j;

/**
 * 用户上下文工具类
 * 用于在业务代码中获取当前登录用户的信息
 */
@Slf4j
public class UserContext {

    /**
     * 使用ThreadLocal存储用户ID，确保线程安全
     */
    private static final ThreadLocal<String> USER_ID = new ThreadLocal<>();

    /**
     * 设置用户ID
     *
     * @param userId 用户ID
     */
    public static void setUserId(String userId) {
        USER_ID.set(userId);
        log.debug("设置用户上下文，用户ID：{}", userId);
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static String getUserId() {
        return USER_ID.get();
    }

    /**
     * 清除用户上下文
     */
    public static void clear() {
        USER_ID.remove();
        log.debug("清除用户上下文");
    }
}