package com.duocai.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 多彩商城启动类
 * @author trae
 */
@SpringBootApplication
@MapperScan("com.duocai.mall.mapper")
public class DuocaiMallApplication {

    public static void main(String[] args) {
        System.out.println("应用启动开始...");
        SpringApplication.run(DuocaiMallApplication.class, args);
    }

}