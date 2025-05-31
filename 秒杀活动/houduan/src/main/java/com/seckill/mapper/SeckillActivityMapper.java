package com.seckill.mapper;

import com.seckill.entity.SeckillActivity;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface SeckillActivityMapper extends BaseMapper<SeckillActivity> {

    /**
     * 查询当前有效的秒杀活动
     * @param currentTime 当前时间
     * @return 活动列表
     */
    List<SeckillActivity> selectByTimeRange(@Param("currentTime") Date currentTime);

    /**
     * 更新活动状态
     * @param id 活动ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}