package com.seckill.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求VO
 */
@Data
public class LoginVO {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;
}