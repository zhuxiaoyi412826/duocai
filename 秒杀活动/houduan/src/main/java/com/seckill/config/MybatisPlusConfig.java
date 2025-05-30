package com.seckill.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus配置类
 */
@Configuration
@EnableTransactionManagement
@MapperScan("com.seckill.mapper")
public class MybatisPlusConfig {

    /**
     * 配置MybatisPlus插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        // 防止全表更新与删除插件
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        
        return interceptor;
    }

    /**
     * SQL性能分析插件
     * 用于输出每条 SQL 语句及其执行时间
     * 开发环境使用，线上不推荐使用
     */
    /*
    @Bean
    @Profile({"dev","test"})
    public PerformanceInterceptor performanceInterceptor() {
        PerformanceInterceptor performanceInterceptor = new PerformanceInterceptor();
        // SQL 执行性能分析，开发环境使用，线上不推荐使用
        performanceInterceptor.setMaxTime(2000);
        // SQL 是否格式化，默认false
        performanceInterceptor.setFormat(true);
        return performanceInterceptor;
    }
    */

    /**
     * 自定义ID生成器
     * 如果需要自定义ID生成策略，可以在这里配置
     */
    /*
    @Bean
    public IdentifierGenerator idGenerator() {
        return new CustomIdGenerator();
    }
    */

    /**
     * 自定义SQL注入器
     * 如果需要自定义SQL注入器，可以在这里配置
     */
    /*
    @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }
    */

    /**
     * 元对象字段填充控制器
     * 用于自动填充创建时间、更新时间等字段
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "deleted", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
            }
        };
    }
}