package com.seckill.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用户信息响应VO
 */
@Data
public class UserInfoVO {

    private String id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private String avatar;
    private Integer gender;
    private Date createTime;
    private Date updateTime;
}