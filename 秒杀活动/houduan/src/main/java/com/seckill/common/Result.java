package com.seckill.common;

import lombok.Data;

/**
 * 统一响应结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> {

    /**
     * 状态码
     */
    private int code;

    /**
     * 消息
     */
    private String message;

    /**
     * 数据
     */
    private T data;

    /**
     * 成功
     */
    private boolean success;

    /**
     * 私有构造方法，使用静态工厂方法创建实例
     */
    private Result() {}

    /**
     * 成功响应，无数据
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 成功响应，有数据
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 成功响应，自定义消息
     */
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    /**
     * 错误响应，默认状态码
     */
    public static <T> Result<T> error(String message) {
        return error(400, message);
    }

    /**
     * 错误响应，自定义状态码和消息
     */
    public static <T> Result<T> error(int code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setSuccess(false);
        return result;
    }

    /**
     * 错误响应，包含数据
     */
    public static <T> Result<T> error(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setSuccess(false);
        return result;
    }

    /**
     * 判断是否成功
     */
    public boolean isSuccess() {
        return this.success;
    }
}