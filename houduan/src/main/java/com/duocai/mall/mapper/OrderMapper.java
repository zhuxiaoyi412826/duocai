package com.duocai.mall.mapper;

import com.duocai.mall.model.Orders;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单数据访问接口
 * @author trae
 */
@Mapper
public interface OrderMapper {
    /**
     * 插入订单
     * @param order 订单信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO orders (user_id, order_no, status, created_at, updated_at) " +
            "VALUES (#{userId}, #{orderNo}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Orders order);
    
    /**
     * 根据ID查询订单
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("SELECT * FROM orders WHERE id = #{id}")
    Orders selectById(Long id);
    
    /**
     * 根据用户ID和订单状态查询订单列表
     * @param userId 用户ID
     * @param status 订单状态
     * @param offset 偏移量
     * @param limit 限制数量
     * @return 订单列表
     */
    @Select("<script>" +
            "SELECT * FROM orders WHERE user_id = #{userId} " +
            "<if test='status != null'>AND status = #{status}</if> " +
            "ORDER BY created_at DESC LIMIT #{offset}, #{limit}" +
            "</script>")
    List<Orders> selectByUserIdAndStatus(@Param("userId") Long userId,
                                      @Param("status") Integer status,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);
    
    /**
     * 更新订单信息
     * @param order 订单信息
     * @return 影响的行数
     */
    @Update("UPDATE orders SET status = #{status}, pay_time = #{payTime}, receive_time = #{receiveTime}, " +
            "updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Orders order);
    
    /**
     * 删除订单
     * @param id 订单ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM orders WHERE id = #{id}")
    int deleteById(Long id);
}