package com.seckill.common;

/**
 * 常用API操作码枚举
 */
public enum ResultCode implements IErrorCode {
    // 成功
    SUCCESS(200, "操作成功"),
    // 失败
    FAILED(500, "操作失败"),
    // 参数检验失败
    VALIDATE_FAILED(404, "参数检验失败"),
    // 未登录
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    // 未授权
    FORBIDDEN(403, "没有相关权限"),
    
    // 用户相关错误码 1000-1999
    USER_NOT_EXIST(1000, "用户不存在"),
    USER_LOGIN_FAILED(1001, "用户名或密码错误"),
    USER_ACCOUNT_FORBIDDEN(1002, "用户账号已被禁用"),
    USER_NOT_LOGIN(1003, "用户未登录"),
    USER_ALREADY_EXIST(1004, "用户已存在"),
    
    // 商品相关错误码 2000-2999
    PRODUCT_NOT_EXIST(2000, "商品不存在"),
    PRODUCT_STOCK_ERROR(2001, "商品库存不足"),
    
    // 订单相关错误码 3000-3999
    ORDER_NOT_EXIST(3000, "订单不存在"),
    ORDER_CREATE_FAILED(3001, "订单创建失败"),
    ORDER_PAY_FAILED(3002, "订单支付失败"),
    ORDER_CANCEL_FAILED(3003, "订单取消失败"),
    ORDER_ALREADY_PAID(3004, "订单已支付"),
    ORDER_ALREADY_CANCEL(3005, "订单已取消"),
    ORDER_TIMEOUT(3006, "订单已超时"),
    
    // 活动相关错误码 4000-4999
    ACTIVITY_NOT_EXIST(4000, "活动不存在"),
    ACTIVITY_NOT_START(4001, "活动未开始"),
    ACTIVITY_ALREADY_END(4002, "活动已结束"),
    ACTIVITY_ALREADY_FULL(4003, "活动已满"),
    ACTIVITY_CREATE_FAILED(4004, "活动创建失败"),
    
    // 秒杀相关错误码 5000-5999
    SECKILL_NOT_START(5000, "秒杀未开始"),
    SECKILL_ALREADY_END(5001, "秒杀已结束"),
    SECKILL_REPEAT(5002, "您已参与过该秒杀"),
    SECKILL_FAILED(5003, "秒杀失败"),
    SECKILL_RATE_LIMIT(5004, "访问太频繁，请稍后再试"),
    
    // 系统错误码 9000-9999
    SYSTEM_ERROR(9000, "系统错误"),
    NETWORK_ERROR(9001, "网络错误"),
    DATABASE_ERROR(9002, "数据库错误"),
    REDIS_ERROR(9003, "Redis错误"),
    LOCK_ERROR(9004, "分布式锁获取失败");

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}