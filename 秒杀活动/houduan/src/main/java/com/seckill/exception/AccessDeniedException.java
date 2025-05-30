package com.seckill.exception;

import lombok.Getter;

/**
 * 权限异常
 */
@Getter
public class AccessDeniedException extends RuntimeException {

    /**
     * 错误码
     */
    private int code;

    /**
     * 错误消息
     */
    private String message;

    public AccessDeniedException(String message) {
        this(403, message);
    }

    public AccessDeniedException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public AccessDeniedException(String message, Throwable cause) {
        this(403, message, cause);
    }

    public AccessDeniedException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}