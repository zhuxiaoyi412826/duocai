package com.seckill.exception;

import com.seckill.common.ApiResponse;
import com.seckill.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<String> handleBusinessException(BusinessException e) {
        log.error("业务异常：", e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理认证异常
     */
    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<String> handleAuthException(AuthException e) {
        log.error("认证异常：", e);
        return ApiResponse.error(e.getCode(), e.getMessage());
    }

    /**
     * 处理参数校验异常（@Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<String>> handleValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：", e);
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessages = bindingResult.getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        return ApiResponse.error(400, "参数校验失败", errorMessages);
    }

    /**
     * 处理参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<String>> handleBindException(BindException e) {
        log.error("参数绑定异常：", e);
        List<String> errorMessages = new ArrayList<>();
        e.getFieldErrors().forEach(error -> 
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage())
        );
        return ApiResponse.error(400, "参数绑定失败", errorMessages);
    }

    /**
     * 处理参数校验异常（@Validated）
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<List<String>> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("参数校验异常：", e);
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        List<String> errorMessages = violations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return ApiResponse.error(400, "参数校验失败", errorMessages);
    }

    /**
     * 处理空指针异常
     */
    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleNullPointerException(NullPointerException e) {
        log.error("空指针异常：", e);
        return ApiResponse.error(Constants.ERROR_CODE, "系统错误，请联系管理员");
    }

    /**
     * 处理其他未知异常
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleException(Exception e) {
        log.error("未知异常：", e);
        return ApiResponse.error(Constants.ERROR_CODE, "系统错误，请联系管理员");
    }

    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<String> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：", e);
        return ApiResponse.error(Constants.ERROR_CODE, "系统错误，请联系管理员");
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("非法参数异常：", e);
        return ApiResponse.error(400, e.getMessage());
    }
}