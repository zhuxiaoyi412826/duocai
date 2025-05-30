package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀订单Mapper接口
 */
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
    
    /**
     * 查询用户在指定活动中的订单
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @return 订单列表
     */
    @Select("SELECT * FROM t_seckill_order WHERE user_id = #{userId} " +
            "AND activity_id = #{activityId}")
    List<SeckillOrder> selectByUserIdAndActivityId(@Param("userId") Long userId, 
                                                  @Param("activityId") Long activityId);

    /**
     * 查询用户的所有秒杀订单
     *
     * @param userId 用户ID
     * @param page 分页参数
     * @return 订单分页数据
     */
    @Select("SELECT * FROM t_seckill_order WHERE user_id = #{userId} " +
            "ORDER BY create_time DESC")
    IPage<SeckillOrder> selectPageByUserId(@Param("userId") Long userId, Page<SeckillOrder> page);

    /**
     * 更新订单状态
     *
     * @param orderId 订单ID
     * @param oldStatus 原状态
     * @param newStatus 新状态
     * @param updateTime 更新时间
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_order SET status = #{newStatus}, " +
            "update_time = #{updateTime} " +
            "WHERE id = #{orderId} AND status = #{oldStatus}")
    int updateStatus(@Param("orderId") Long orderId, 
                    @Param("oldStatus") Integer oldStatus, 
                    @Param("newStatus") Integer newStatus, 
                    @Param("updateTime") LocalDateTime updateTime);

    /**
     * 更新订单支付信息
     *
     * @param orderId 订单ID
     * @param payTime 支付时间
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_order SET status = 1, pay_time = #{payTime}, " +
            "update_time = #{payTime} WHERE id = #{orderId} AND status = 0")
    int updatePayInfo(@Param("orderId") Long orderId, @Param("payTime") LocalDateTime payTime);

    /**
     * 查询超时未支付的订单
     *
     * @param timeout 超时时间点
     * @return 订单列表
     */
    @Select("SELECT * FROM t_seckill_order WHERE status = 0 " +
            "AND create_time <= #{timeout}")
    List<SeckillOrder> selectTimeoutOrders(@Param("timeout") LocalDateTime timeout);

    /**
     * 查询用户在指定商品的秒杀活动中是否已下单
     *
     * @param userId 用户ID
     * @param activityId 活动ID
     * @param productId 商品ID
     * @return 订单信息
     */
    @Select("SELECT * FROM t_seckill_order WHERE user_id = #{userId} " +
            "AND activity_id = #{activityId} AND product_id = #{productId} " +
            "AND status != 2")
    SeckillOrder selectExistingOrder(@Param("userId") Long userId, 
                                    @Param("activityId") Long activityId, 
                                    @Param("productId") Long productId);
}