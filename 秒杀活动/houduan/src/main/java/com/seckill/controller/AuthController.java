package com.seckill.controller;

import com.seckill.common.Result;
import com.seckill.service.AuthService;
import com.seckill.vo.request.LoginRequest;
import com.seckill.vo.request.RegisterRequest;
import com.seckill.vo.response.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * 认证控制器
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/api/auth")
@Api(tags = "认证管理")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Long> register(@Valid @RequestBody RegisterRequest request) {
        log.info("User registration request received for username: {}", request.getUsername());
        Long userId = authService.register(request);
        return Result.success(userId, "注册成功");
    }

    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login attempt for username: {}", request.getUsername());
        LoginResponse response = authService.login(request);
        return Result.success(response, "登录成功");
    }

    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        String token = extractToken(request);
        if (token != null) {
            authService.logout(token);
        }
        return Result.success(null, "登出成功");
    }

    @PostMapping("/refresh-token")
    @ApiOperation("刷新令牌")
    public Result<String> refreshToken(@RequestHeader("Authorization") String authorization) {
        String oldToken = extractToken(authorization);
        if (oldToken == null) {
            return Result.error("无效的令牌");
        }
        String newToken = authService.refreshToken(oldToken);
        return Result.success(newToken, "令牌刷新成功");
    }

    @GetMapping("/check-username")
    @ApiOperation("检查用户名是否存在")
    public Result<Boolean> checkUsername(@RequestParam @NotBlank String username) {
        boolean exists = authService.isUsernameExists(username);
        return Result.success(exists, exists ? "用户名已存在" : "用户名可用");
    }

    @PostMapping("/change-password")
    @ApiOperation("修改密码")
    public Result<Void> changePassword(
            @RequestParam @NotBlank String oldPassword,
            @RequestParam @NotBlank String newPassword) {
        Long userId = authService.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户未登录");
        }
        authService.changePassword(userId, oldPassword, newPassword);
        return Result.success(null, "密码修改成功");
    }

    /**
     * 从请求头中提取token
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return extractToken(bearerToken);
    }

    /**
     * 从Authorization头中提取token
     */
    private String extractToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}