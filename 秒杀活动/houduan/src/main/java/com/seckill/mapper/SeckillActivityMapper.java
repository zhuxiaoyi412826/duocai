package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.SeckillActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 秒杀活动Mapper接口
 */
@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {
    
    /**
     * 查询正在进行的秒杀活动
     *
     * @return 活动列表
     */
    @Select("SELECT * FROM t_seckill_activity WHERE status = 1 " +
            "AND start_time <= #{currentTime} AND end_time > #{currentTime} " +
            "ORDER BY start_time ASC")
    List<SeckillActivity> selectOngoingActivities(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 查询即将开始的秒杀活动
     *
     * @param hours 未来几小时内
     * @return 活动列表
     */
    @Select("SELECT * FROM t_seckill_activity WHERE status = 0 " +
            "AND start_time > #{currentTime} " +
            "AND start_time <= DATE_ADD(#{currentTime}, INTERVAL #{hours} HOUR) " +
            "ORDER BY start_time ASC")
    List<SeckillActivity> selectUpcomingActivities(@Param("currentTime") LocalDateTime currentTime, 
                                                  @Param("hours") Integer hours);

    /**
     * 分页查询所有秒杀活动
     *
     * @param page 分页参数
     * @return 活动分页数据
     */
    @Select("SELECT * FROM t_seckill_activity ORDER BY create_time DESC")
    IPage<SeckillActivity> selectActivitiesPage(Page<SeckillActivity> page);

    /**
     * 更新活动状态
     *
     * @param activityId 活动ID
     * @param status 状态
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_activity SET status = #{status}, " +
            "update_time = #{updateTime} WHERE id = #{activityId}")
    int updateStatus(@Param("activityId") Long activityId, 
                    @Param("status") Integer status, 
                    @Param("updateTime") LocalDateTime updateTime);

    /**
     * 查询指定商品相关的秒杀活动
     *
     * @param productId 商品ID
     * @return 活动列表
     */
    @Select("SELECT a.* FROM t_seckill_activity a " +
            "INNER JOIN t_seckill_product p ON a.id = p.activity_id " +
            "WHERE p.product_id = #{productId} AND a.status IN (0, 1) " +
            "ORDER BY a.start_time ASC")
    List<SeckillActivity> selectByProductId(@Param("productId") Long productId);
}