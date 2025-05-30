package com.seckill.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.management.ManagementFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 监控配置类
 */
@Configuration
public class MonitorConfig {

    /**
     * 配置指标注册表
     */
    @Bean
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    /**
     * 自定义指标注册表配置
     */
    @Bean
    public MeterRegistryCustomizer<MeterRegistry> metricsCommon() {
        return registry -> registry.config().commonTags("application", "seckill");
    }

    /**
     * 数据库健康检查指示器
     */
    @Bean
    public HealthIndicator dbHealthIndicator() {
        return () -> {
            // 这里可以添加数据库连接检查逻辑
            return Health.up()
                    .withDetail("database", "MySQL")
                    .withDetail("status", "connected")
                    .build();
        };
    }

    /**
     * Redis健康检查指示器
     */
    @Bean
    public HealthIndicator redisHealthIndicator() {
        return () -> {
            // 这里可以添加Redis连接检查逻辑
            return Health.up()
                    .withDetail("redis", "connected")
                    .withDetail("status", "running")
                    .build();
        };
    }

    /**
     * 系统负载监控指示器
     */
    @Bean
    public HealthIndicator systemLoadHealthIndicator() {
        return () -> {
            double systemLoad = getSystemLoad();
            return systemLoad < 0.8 ?
                    Health.up()
                            .withDetail("systemLoad", systemLoad)
                            .build() :
                    Health.down()
                            .withDetail("systemLoad", systemLoad)
                            .withDetail("message", "System load is too high")
                            .build();
        };
    }

    /**
     * JVM内存监控指示器
     */
    @Bean
    public HealthIndicator jvmMemoryHealthIndicator() {
        return () -> {
            long maxMemory = Runtime.getRuntime().maxMemory();
            long totalMemory = Runtime.getRuntime().totalMemory();
            long freeMemory = Runtime.getRuntime().freeMemory();
            double memoryUsage = (double) (totalMemory - freeMemory) / maxMemory;

            return memoryUsage < 0.9 ?
                    Health.up()
                            .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage * 100))
                            .withDetail("freeMemory", freeMemory)
                            .withDetail("totalMemory", totalMemory)
                            .withDetail("maxMemory", maxMemory)
                            .build() :
                    Health.down()
                            .withDetail("memoryUsage", String.format("%.2f%%", memoryUsage * 100))
                            .withDetail("message", "Memory usage is too high")
                            .build();
        };
    }

    /**
     * 秒杀系统性能指标收集器
     */
    @Bean
    public void seckillMetrics(MeterRegistry registry) {
        // 注册秒杀请求计数器
        AtomicInteger seckillRequests = registry.gauge("seckill.requests", new AtomicInteger(0));
        
        // 注册秒杀成功计数器
        AtomicInteger seckillSuccess = registry.gauge("seckill.success", new AtomicInteger(0));
        
        // 注册库存计数器
        AtomicInteger stockCount = registry.gauge("seckill.stock", new AtomicInteger(0));
        
        // 注册订单创建计数器
        AtomicInteger orderCreated = registry.gauge("seckill.orders.created", new AtomicInteger(0));
        
        // 注册支付完成计数器
        AtomicInteger paymentCompleted = registry.gauge("seckill.payment.completed", new AtomicInteger(0));
    }

    /**
     * 获取系统负载
     */
    private double getSystemLoad() {
        return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
    }
}