package com.seckill.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 数据源配置类
 */
@Configuration
public class DataSourceConfig {

    /**
     * 配置Druid数据源
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    /**
     * 配置Druid监控页面
     */
    @Bean
    public ServletRegistrationBean<StatViewServlet> statViewServlet() {
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        Map<String, String> initParams = new HashMap<>();
        // 监控页面登录用户名
        initParams.put("loginUsername", "admin");
        // 监控页面登录密码
        initParams.put("loginPassword", "admin");
        // IP白名单（没有配置或者为空，则允许所有访问）
        initParams.put("allow", "");
        // IP黑名单（存在共同时，deny优先于allow）
        initParams.put("deny", "");
        // 是否能够重置数据
        initParams.put("resetEnable", "false");
        bean.setInitParameters(initParams);
        return bean;
    }

    /**
     * 配置Druid监控过滤器
     */
    @Bean
    public FilterRegistrationBean<WebStatFilter> webStatFilter() {
        FilterRegistrationBean<WebStatFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(new WebStatFilter());
        // 添加过滤规则
        bean.setUrlPatterns(Arrays.asList("/*"));
        // 添加不需要忽略的格式信息
        Map<String, String> initParams = new HashMap<>();
        initParams.put("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        // 开启session统计功能
        initParams.put("sessionStatEnable", "true");
        // 配置session统计最大个数
        initParams.put("sessionStatMaxCount", "1000");
        // 配置profileEnable能够监控单个url调用的sql列表
        initParams.put("profileEnable", "true");
        bean.setInitParameters(initParams);
        return bean;
    }
}