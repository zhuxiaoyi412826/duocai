package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 秒杀商品Mapper接口
 */
@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {
    
    /**
     * 查询活动下的所有秒杀商品
     *
     * @param activityId 活动ID
     * @return 秒杀商品列表
     */
    @Select("SELECT * FROM t_seckill_product WHERE activity_id = #{activityId}")
    List<SeckillProduct> selectByActivityId(@Param("activityId") Long activityId);

    /**
     * 分页查询活动下的秒杀商品
     *
     * @param activityId 活动ID
     * @param page 分页参数
     * @return 秒杀商品分页数据
     */
    @Select("SELECT * FROM t_seckill_product WHERE activity_id = #{activityId}")
    IPage<SeckillProduct> selectPageByActivityId(@Param("activityId") Long activityId, Page<SeckillProduct> page);

    /**
     * 扣减秒杀商品库存（乐观锁）
     *
     * @param id 秒杀商品ID
     * @param quantity 扣减数量
     * @param version 版本号
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_product SET available_stock = available_stock - #{quantity}, " +
            "sold_num = sold_num + #{quantity}, version = version + 1 " +
            "WHERE id = #{id} AND version = #{version} AND available_stock >= #{quantity}")
    int deductStock(@Param("id") Long id, 
                    @Param("quantity") Integer quantity, 
                    @Param("version") Integer version);

    /**
     * 恢复秒杀商品库存（订单取消时）
     *
     * @param id 秒杀商品ID
     * @param quantity 恢复数量
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_product SET available_stock = available_stock + #{quantity}, " +
            "sold_num = sold_num - #{quantity} " +
            "WHERE id = #{id}")
    int restoreStock(@Param("id") Long id, @Param("quantity") Integer quantity);

    /**
     * 更新秒杀商品状态
     *
     * @param id 秒杀商品ID
     * @param status 状态
     * @return 更新结果
     */
    @Update("UPDATE t_seckill_product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 查询商品在指定活动中的秒杀信息
     *
     * @param productId 商品ID
     * @param activityId 活动ID
     * @return 秒杀商品信息
     */
    @Select("SELECT * FROM t_seckill_product WHERE product_id = #{productId} " +
            "AND activity_id = #{activityId}")
    SeckillProduct selectByProductIdAndActivityId(@Param("productId") Long productId, 
                                                @Param("activityId") Long activityId);
}