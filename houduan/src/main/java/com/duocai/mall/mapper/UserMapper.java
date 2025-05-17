package com.duocai.mall.mapper;

import com.duocai.mall.model.Users;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 用户Mapper接口
 * @author trae
 */
@Mapper
public interface UserMapper {
    /**
     * 新增用户
     * @param user 用户信息
     * @return 影响行数
     */
    @Insert("INSERT INTO users(username, password, email, phone, status, created_at) " +
            "VALUES(#{username}, #{password}, #{email}, #{phone}, #{status}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Users user);
    
    /**
     * 根据ID查询用户
     * @param id 用户ID
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE id = #{id}")
    Users selectById(Long id);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Select("SELECT * FROM users WHERE username = #{username}")
    Users selectByUsername(String username);
    
    /**
     * 更新用户信息
     * @param user 用户信息
     * @return 影响行数
     */
    @Update("UPDATE users SET username = #{username}, email = #{email}, " +
            "phone = #{phone}, status = #{status} WHERE id = #{id}")
    int update(Users user);
    
    /**
     * 修改密码
     * @param id 用户ID
     * @param password 新密码
     * @return 影响行数
     */
    @Update("UPDATE users SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);
    
    /**
     * 删除用户
     * @param id 用户ID
     * @return 影响行数
     */
    @Delete("DELETE FROM users WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 查询用户列表
     * @return 用户列表
     */
    @Select("SELECT * FROM users")
    List<Users> selectAll();
}