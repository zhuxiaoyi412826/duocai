package com.seckill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Swagger配置类
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${springfox.documentation.enabled:true}")
    private boolean enabled;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enabled)
                .apiInfo(apiInfo())
                .select()
                // 指定扫描的包路径
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                // 指定路径处理PathSelectors.any()代表所有的路径
                .paths(PathSelectors.any())
                .build()
                // 添加登录认证
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("秒杀系统API文档")
                .description("秒杀系统后端接口文档")
                .contact(new Contact("开发团队", "http://localhost:8080", "support@example.com"))
                .version("1.0.0")
                .build();
    }

    private List<SecurityScheme> securitySchemes() {
        // 设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }

    private List<SecurityContext> securityContexts() {
        // 设置需要登录认证的路径
        List<SecurityContext> result = new ArrayList<>();
        result.add(getContextByPath("/api/.*"));
        return result;
    }

    private SecurityContext getContextByPath(String pathRegex) {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex(pathRegex))
                .build();
    }

    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> result = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        result.add(new SecurityReference("Authorization", authorizationScopes));
        return result;
    }

    /**
     * 自定义API文档的分组
     */
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("用户接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                .paths(PathSelectors.ant("/api/user/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket authApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("认证接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                .paths(PathSelectors.ant("/api/auth/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("商品接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                .paths(PathSelectors.ant("/api/product/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket orderApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("订单接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                .paths(PathSelectors.ant("/api/order/**"))
                .build()
                .apiInfo(apiInfo());
    }

    @Bean
    public Docket seckillApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("秒杀接口")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.seckill.controller"))
                .paths(PathSelectors.ant("/api/seckill/**"))
                .build()
                .apiInfo(apiInfo());
    }
}