package com.duocai.mall.model;

import lombok.Data;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Users {
    private Long id;
    private String username;

    private String password;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createdAt;
}