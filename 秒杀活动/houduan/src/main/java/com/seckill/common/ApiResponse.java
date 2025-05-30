package com.seckill.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
@Data
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 私有构造方法
     */
    private ApiResponse() {
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构造方法
     */
    private ApiResponse(int code, String message, T data, boolean success) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.success = success;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 成功返回结果
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE, null, true);
    }

    /**
     * 成功返回结果
     *
     * @param data 返回数据
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(Constants.SUCCESS_CODE, Constants.SUCCESS_MESSAGE, data, true);
    }

    /**
     * 成功返回结果
     *
     * @param data    返回数据
     * @param message 返回消息
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(Constants.SUCCESS_CODE, message, data, true);
    }

    /**
     * 失败返回结果
     */
    public static <T> ApiResponse<T> error() {
        return new ApiResponse<>(Constants.ERROR_CODE, Constants.ERROR_MESSAGE, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param message 错误消息
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(Constants.ERROR_CODE, message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误消息
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, false);
    }

    /**
     * 失败返回结果
     *
     * @param code    错误码
     * @param message 错误消息
     * @param data    错误数据
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, false);
    }

    /**
     * 参数验证失败返回结果
     */
    public static <T> ApiResponse<T> validateFailed() {
        return error(400, "参数验证失败");
    }

    /**
     * 参数验证失败返回结果
     *
     * @param message 提示信息
     */
    public static <T> ApiResponse<T> validateFailed(String message) {
        return error(400, message);
    }

    /**
     * 未登录返回结果
     */
    public static <T> ApiResponse<T> unauthorized() {
        return error(Constants.UNAUTHORIZED_CODE, Constants.UNAUTHORIZED_MESSAGE);
    }

    /**
     * 未授权返回结果
     */
    public static <T> ApiResponse<T> forbidden() {
        return error(Constants.FORBIDDEN_CODE, Constants.FORBIDDEN_MESSAGE);
    }

    /**
     * 资源不存在返回结果
     */
    public static <T> ApiResponse<T> notFound() {
        return error(Constants.NOT_FOUND_CODE, Constants.NOT_FOUND_MESSAGE);
    }

    /**
     * 请求过于频繁返回结果
     */
    public static <T> ApiResponse<T> tooManyRequests() {
        return error(Constants.TOO_MANY_REQUESTS_CODE, Constants.TOO_MANY_REQUESTS_MESSAGE);
    }
}