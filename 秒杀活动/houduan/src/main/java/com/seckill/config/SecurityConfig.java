package com.seckill.config;

import com.seckill.security.XssFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import java.util.HashMap;
import java.util.Map;

/**
 * 安全配置类
 */
@Configuration
public class SecurityConfig {

    /**
     * 配置密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置XSS过滤器
     */
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);
        
        Map<String, String> initParameters = new HashMap<>();
        // 配置不需要过滤的路径
        initParameters.put("excludes", "/favicon.ico,/img/*,/js/*,/css/*");
        // 配置不需要过滤的参数
        initParameters.put("excludeParams", "rich_text_content");
        registration.setInitParameters(initParameters);
        
        return registration;
    }
}