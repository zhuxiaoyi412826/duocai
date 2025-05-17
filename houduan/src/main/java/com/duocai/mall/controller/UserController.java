package com.duocai.mall.controller;

import com.duocai.mall.model.Users;
import com.duocai.mall.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * @author trae
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user) {
        Users registeredUser = userService.register(user);
        return ResponseEntity.ok(registeredUser);
    }
    
    /**
     * 用户登录
     * @param loginRequest 登录请求
     * @return JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String token = userService.login(loginRequest.get("username"), loginRequest.get("password"));
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id) {
        Users user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
    
    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Users user) {
        user.setId(id);
        Users updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }
    
    /**
     * 修改密码
     * @param id 用户ID
     * @param passwordRequest 密码修改请求
     * @return 修改结果
     */
    @PutMapping("/{id}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordRequest) {
        boolean success = userService.changePassword(id,
                passwordRequest.get("oldPassword"),
                passwordRequest.get("newPassword"));
        return ResponseEntity.ok(Map.of("success", success));
    }
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        boolean success = userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
}