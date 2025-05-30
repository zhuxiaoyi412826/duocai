package com.seckill.common;

/**
 * 系统常量类
 */
public class Constants {

    // 请求头相关常量
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String CURRENT_USER_ID = "currentUserId";
    public static final String CURRENT_USER = "currentUser";

    // Redis key前缀
    public static final String REDIS_USER_KEY = "user:%s";
    public static final String REDIS_ACCESS_LIMIT_KEY = "access_limit:%s";
    public static final String REDIS_CAPTCHA_KEY = "captcha:%s";
    public static final String REDIS_SECKILL_GOODS_KEY = "seckill:goods:%s";
    public static final String REDIS_SECKILL_STOCK_KEY = "seckill:stock:%s";
    public static final String REDIS_SECKILL_RESULT_KEY = "seckill:result:%s:%s";
    public static final String REDIS_SECKILL_ORDER_KEY = "seckill:order:%s:%s";

    // Token相关常量
    public static final long TOKEN_EXPIRATION = 3600 * 24; // 24小时
    public static final long TOKEN_REFRESH_INTERVAL = 3600 * 2; // 2小时

    // 验证码相关常量
    public static final int CAPTCHA_EXPIRE_SECONDS = 300; // 5分钟
    public static final int CAPTCHA_LENGTH = 4;

    // 秒杀相关常量
    public static final int SECKILL_GOODS_EXPIRE_SECONDS = 3600 * 24; // 24小时
    public static final int SECKILL_ORDER_EXPIRE_SECONDS = 3600 * 24; // 24小时

    // 用户状态
    public static final int USER_STATUS_DISABLED = 0;
    public static final int USER_STATUS_ENABLED = 1;

    // 删除状态
    public static final int DELETED_NO = 0;
    public static final int DELETED_YES = 1;

    // 性别
    public static final int GENDER_UNKNOWN = 0;
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    // 默认角色
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    // 默认权限
    public static final String PERMISSION_VIEW = "user:view";
    public static final String PERMISSION_EDIT = "user:edit";
    public static final String PERMISSION_ALL = "*";
}