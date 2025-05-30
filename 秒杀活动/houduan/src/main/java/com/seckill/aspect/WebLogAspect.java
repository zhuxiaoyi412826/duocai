package com.seckill.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seckill.util.IpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Web日志切面
 */
@Slf4j
@Aspect
@Component
public class WebLogAspect {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 以 controller 包下定义的所有请求为切入点
     */
    @Pointcut("execution(public * com.seckill.controller..*.*(..))")
    public void webLog() {
    }

    /**
     * 在切点之前织入
     */
    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) throws Throwable {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }
        
        HttpServletRequest request = attributes.getRequest();
        
        // 打印请求相关参数
        log.info("========================================== Start ==========================================");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURL().toString());
        // 打印 HTTP method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}", IpUtil.getIpAddr(request));
        // 打印请求入参
        List<Object> args = Arrays.stream(joinPoint.getArgs())
                .filter(arg -> !(arg instanceof HttpServletRequest))
                .filter(arg -> !(arg instanceof HttpServletResponse))
                .filter(arg -> !(arg instanceof MultipartFile))
                .collect(Collectors.toList());
        try {
            log.info("Request Args   : {}", objectMapper.writeValueAsString(args));
        } catch (Exception e) {
            log.info("Request Args   : {}", args);
        }
    }

    /**
     * 在切点之后织入
     */
    @After("webLog()")
    public void doAfter() {
        log.info("=========================================== End ===========================================");
    }

    /**
     * 环绕
     */
    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        try {
            log.info("Response Args  : {}", objectMapper.writeValueAsString(result));
        } catch (Exception e) {
            log.info("Response Args  : {}", result);
        }
        // 执行耗时
        log.info("Time-Consuming : {} ms", System.currentTimeMillis() - startTime);
        
        return result;
    }
}