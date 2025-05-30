package com.seckill.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.seckill.entity.SeckillActivity;
import com.seckill.vo.SeckillActivityVO;

import java.util.List;

/**
 * 秒杀活动服务接口
 */
public interface SeckillActivityService {
    
    /**
     * 创建秒杀活动
     *
     * @param activity 活动信息
     * @return 创建结果
     */
    boolean createActivity(SeckillActivity activity);
    
    /**
     * 更新秒杀活动
     *
     * @param activity 活动信息
     * @return 更新结果
     */
    boolean updateActivity(SeckillActivity activity);
    
    /**
     * 删除秒杀活动
     *
     * @param activityId 活动ID
     * @return 删除结果
     */
    boolean deleteActivity(Long activityId);
    
    /**
     * 获取秒杀活动详情
     *
     * @param activityId 活动ID
     * @return 活动详情
     */
    SeckillActivityVO getActivityDetail(Long activityId);
    
    /**
     * 分页查询所有秒杀活动
     *
     * @param page 页码
     * @param size 每页大小
     * @return 活动分页数据
     */
    IPage<SeckillActivityVO> getActivitiesPage(Integer page, Integer size);
    
    /**
     * 查询正在进行的秒杀活动
     *
     * @return 活动列表
     */
    List<SeckillActivityVO> getOngoingActivities();
    
    /**
     * 查询即将开始的秒杀活动
     *
     * @param hours 未来几小时内
     * @return 活动列表
     */
    List<SeckillActivityVO> getUpcomingActivities(Integer hours);
    
    /**
     * 更新活动状态
     *
     * @param activityId 活动ID
     * @param status 状态
     * @return 更新结果
     */
    boolean updateActivityStatus(Long activityId, Integer status);
    
    /**
     * 查询指定商品相关的秒杀活动
     *
     * @param productId 商品ID
     * @return 活动列表
     */
    List<SeckillActivityVO> getActivitiesByProductId(Long productId);
    
    /**
     * 检查活动是否存在且有效
     *
     * @param activityId 活动ID
     * @return 检查结果
     */
    boolean checkActivityValid(Long activityId);
    
    /**
     * 检查活动是否在进行中
     *
     * @param activityId 活动ID
     * @return 检查结果
     */
    boolean checkActivityOngoing(Long activityId);
    
    /**
     * 发布活动（预热缓存）
     *
     * @param activityId 活动ID
     * @return 发布结果
     */
    boolean publishActivity(Long activityId);
}