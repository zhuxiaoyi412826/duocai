package com.duocai.mall.common.api;

/**
 * 错误码接口
 * @author trae
 */
public interface IErrorCode {
    /**
     * 获取错误码
     */
    long getCode();

    /**
     * 获取错误信息
     */
    String getMessage();
}