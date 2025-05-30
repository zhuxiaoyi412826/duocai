package com.seckill.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.seckill.entity.SeckillActivity;
import com.seckill.mapper.SeckillActivityMapper;
import com.seckill.service.SeckillActivityService;
import com.seckill.service.SeckillProductService;
import com.seckill.vo.SeckillActivityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 秒杀活动服务实现类
 */
@Service
public class SeckillActivityServiceImpl extends ServiceImpl<SeckillActivityMapper, SeckillActivity> implements SeckillActivityService {

    @Autowired
    private SeckillActivityMapper activityMapper;

    @Autowired
    private SeckillProductService seckillProductService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String ACTIVITY_CACHE_PREFIX = "seckill:activity:";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createActivity(SeckillActivity activity) {
        activity.setCreateTime(LocalDateTime.now());
        activity.setUpdateTime(LocalDateTime.now());
        return save(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_activity", key = "#activity.id")
    public boolean updateActivity(SeckillActivity activity) {
        activity.setUpdateTime(LocalDateTime.now());
        return updateById(activity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_activity", key = "#activityId")
    public boolean deleteActivity(Long activityId) {
        // 删除活动相关的秒杀商品
        seckillProductService.deleteByActivityId(activityId);
        // 删除活动
        return removeById(activityId);
    }

    @Override
    @Cacheable(value = "seckill_activity", key = "#activityId", unless = "#result == null")
    public SeckillActivityVO getActivityDetail(Long activityId) {
        SeckillActivity activity = getById(activityId);
        if (activity == null) {
            return null;
        }
        return convertToVO(activity);
    }

    @Override
    public IPage<SeckillActivityVO> getActivitiesPage(Integer page, Integer size) {
        Page<SeckillActivity> activityPage = new Page<>(page, size);
        IPage<SeckillActivity> activityIPage = activityMapper.selectActivitiesPage(activityPage);
        return convertToVOPage(activityIPage);
    }

    @Override
    public List<SeckillActivityVO> getOngoingActivities() {
        List<SeckillActivity> activities = activityMapper.selectOngoingActivities(LocalDateTime.now());
        return activities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SeckillActivityVO> getUpcomingActivities(Integer hours) {
        List<SeckillActivity> activities = activityMapper.selectUpcomingActivities(LocalDateTime.now(), hours);
        return activities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "seckill_activity", key = "#activityId")
    public boolean updateActivityStatus(Long activityId, Integer status) {
        return activityMapper.updateStatus(activityId, status, LocalDateTime.now()) > 0;
    }

    @Override
    public List<SeckillActivityVO> getActivitiesByProductId(Long productId) {
        List<SeckillActivity> activities = activityMapper.selectByProductId(productId);
        return activities.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkActivityValid(Long activityId) {
        SeckillActivity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        return activity.getStatus() != 2; // 2表示已结束
    }

    @Override
    public boolean checkActivityOngoing(Long activityId) {
        SeckillActivity activity = getById(activityId);
        if (activity == null) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return activity.getStatus() == 1 && // 1表示进行中
                activity.getStartTime().isBefore(now) &&
                activity.getEndTime().isAfter(now);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean publishActivity(Long activityId) {
        // 获取活动信息
        SeckillActivity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }

        // 预热活动信息到Redis
        String activityKey = ACTIVITY_CACHE_PREFIX + activityId;
        redisTemplate.opsForValue().set(activityKey, convertToVO(activity));

        // 预热该活动下的所有商品信息
        seckillProductService.preloadProductsForActivity(activityId);

        // 更新活动状态为待开始
        return updateActivityStatus(activityId, 0);
    }

    /**
     * 将活动实体转换为VO对象
     */
    private SeckillActivityVO convertToVO(SeckillActivity activity) {
        if (activity == null) {
            return null;
        }
        SeckillActivityVO activityVO = new SeckillActivityVO();
        BeanUtils.copyProperties(activity, activityVO);
        return activityVO;
    }

    /**
     * 将活动分页数据转换为VO分页数据
     */
    private IPage<SeckillActivityVO> convertToVOPage(IPage<SeckillActivity> activityPage) {
        IPage<SeckillActivityVO> voPage = new Page<>(activityPage.getCurrent(), activityPage.getSize(), activityPage.getTotal());
        List<SeckillActivityVO> voList = activityPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }
}