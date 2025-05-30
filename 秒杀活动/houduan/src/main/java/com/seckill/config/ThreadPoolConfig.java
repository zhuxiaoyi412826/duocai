package com.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ThreadPoolConfig {

    /**
     * 配置异步任务执行器
     */
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(10);
        
        // 最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(20);
        
        // 缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(200);
        
        // 允许线程的空闲时间：当超过了核心线程数之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        
        // 线程池名的前缀：设置好了之后可以方便定位处理任务所在的线程池
        executor.setThreadNamePrefix("taskExecutor-");
        
        // 缓冲队列满了之后的拒绝策略：由调用线程处理（一般是主线程）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务结束后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.initialize();
        return executor;
    }

    /**
     * 配置定时任务执行器
     */
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        
        // 线程池大小
        scheduler.setPoolSize(5);
        
        // 线程名前缀
        scheduler.setThreadNamePrefix("taskScheduler-");
        
        // 等待终止的时间，单位秒
        scheduler.setAwaitTerminationSeconds(60);
        
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        
        // 线程池关闭时等待所有任务完成
        scheduler.setAwaitTerminationSeconds(60);
        
        // 设置当任务被取消的策略
        scheduler.setErrorHandler(throwable -> {
            // 记录错误日志
            System.err.println("Scheduled task error: " + throwable.getMessage());
        });
        
        // 设置当任务抛出异常时的处理策略
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        return scheduler;
    }

    /**
     * 配置秒杀专用线程池
     * 用于处理秒杀相关的高并发任务
     */
    @Bean(name = "seckillExecutor")
    public ThreadPoolTaskExecutor seckillExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数设置较大，以处理突发的高并发请求
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(40);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("seckill-");
        
        // 使用CallerRunsPolicy，防止系统崩溃
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.initialize();
        return executor;
    }

    /**
     * 配置订单处理线程池
     * 用于处理订单相关的异步任务
     */
    @Bean(name = "orderExecutor")
    public ThreadPoolTaskExecutor orderExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("order-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        executor.initialize();
        return executor;
    }
}