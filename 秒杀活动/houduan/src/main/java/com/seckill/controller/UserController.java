package com.seckill.controller;

import com.seckill.annotation.AccessLimit;
import com.seckill.common.Constants;
import com.seckill.common.Result;
import com.seckill.entity.User;
import com.seckill.exception.AuthException;
import com.seckill.service.UserService;
import com.seckill.util.JwtUtil;
import com.seckill.vo.LoginVO;
import com.seckill.vo.RegisterVO;
import com.seckill.vo.UserInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    @AccessLimit(seconds = 60, maxCount = 3, needLogin = false)
    public Result<String> register(@RequestBody @Valid RegisterVO registerVO) {
        userService.register(registerVO);
        return Result.success("注册成功");
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    @AccessLimit(seconds = 60, maxCount = 5, needLogin = false)
    public Result<String> login(@RequestBody @Valid LoginVO loginVO, HttpServletRequest request) {
        User user = userService.login(loginVO);
        String token = jwtUtil.generateToken(user.getId());
        return Result.success(token);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constants.CURRENT_USER_ID);
        User user = userService.getById(userId);
        if (user == null) {
            throw AuthException.unauthorized();
        }

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(user, userInfoVO);
        return Result.success(userInfoVO);
    }

    /**
     * 刷新Token
     */
    @GetMapping("/refreshToken")
    public Result<String> refreshToken(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constants.CURRENT_USER_ID);
        String newToken = jwtUtil.refreshToken(request.getHeader(jwtUtil.getHeader()));
        return Result.success(newToken);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request) {
        String userId = (String) request.getAttribute(Constants.CURRENT_USER_ID);
        userService.logout(userId);
        return Result.success();
    }
}