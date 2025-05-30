package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.seckill.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 根据角色查询用户列表
     *
     * @param role 角色
     * @return 用户列表
     */
    @Select("SELECT * FROM t_user WHERE FIND_IN_SET(#{role}, roles) AND is_deleted = 0")
    List<User> selectByRole(@Param("role") String role);

    /**
     * 更新用户最后登录信息
     *
     * @param userId 用户ID
     * @param ip     登录IP
     * @return 影响行数
     */
    @Update("UPDATE t_user SET last_login_time = NOW(), last_login_ip = #{ip} WHERE id = #{userId}")
    int updateLoginInfo(@Param("userId") String userId, @Param("ip") String ip);

    /**
     * 更新用户状态
     *
     * @param userId 用户ID
     * @param status 状态（0-禁用，1-正常）
     * @return 影响行数
     */
    @Update("UPDATE t_user SET status = #{status}, update_time = NOW() WHERE id = #{userId}")
    int updateStatus(@Param("userId") String userId, @Param("status") Integer status);

    /**
     * 批量更新用户状态
     *
     * @param userIds 用户ID列表
     * @param status  状态（0-禁用，1-正常）
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE t_user SET status = #{status}, update_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchUpdateStatus(@Param("userIds") List<String> userIds, @Param("status") Integer status);

    /**
     * 逻辑删除用户
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    @Update("UPDATE t_user SET is_deleted = 1, update_time = NOW() WHERE id = #{userId}")
    int logicDelete(@Param("userId") String userId);

    /**
     * 批量逻辑删除用户
     *
     * @param userIds 用户ID列表
     * @return 影响行数
     */
    @Update("<script>" +
            "UPDATE t_user SET is_deleted = 1, update_time = NOW() " +
            "WHERE id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    int batchLogicDelete(@Param("userIds") List<String> userIds);

    /**
     * 更新用户角色
     *
     * @param userId 用户ID
     * @param roles  角色（多个角色用逗号分隔）
     * @return 影响行数
     */
    @Update("UPDATE t_user SET roles = #{roles}, update_time = NOW() WHERE id = #{userId}")
    int updateRoles(@Param("userId") String userId, @Param("roles") String roles);

    /**
     * 统计指定角色的用户数量
     *
     * @param role 角色
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE FIND_IN_SET(#{role}, roles) AND is_deleted = 0")
    int countByRole(@Param("role") String role);

    /**
     * 查询指定时间段内注册的用户数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 用户数量
     */
    @Select("SELECT COUNT(*) FROM t_user WHERE create_time BETWEEN #{startTime} AND #{endTime} AND is_deleted = 0")
    int countByCreateTime(@Param("startTime") String startTime, @Param("endTime") String endTime);
}