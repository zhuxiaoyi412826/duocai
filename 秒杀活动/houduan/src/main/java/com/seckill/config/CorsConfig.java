package com.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 */
@Configuration
public class CorsConfig {

    /**
     * 配置跨域过滤器
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许跨域的头部信息
        config.addAllowedHeader("*");
        // 允许跨域的方法
        config.addAllowedMethod("*");
        // 允许跨域的域名，* 表示允许任何域名使用
        config.addAllowedOriginPattern("*");
        // 是否允许携带cookie信息
        config.setAllowCredentials(true);
        // 跨域允许时间
        config.setMaxAge(3600L);
        
        // 添加映射路径，拦截一切请求
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}