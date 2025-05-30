package com.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.seckill.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商品Mapper接口
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 分页查询在售商品
     *
     * @param page 分页参数
     * @return 商品分页数据
     */
    @Select("SELECT * FROM t_product WHERE status = 1 ORDER BY create_time DESC")
    IPage<Product> selectOnSaleProducts(Page<Product> page);

    /**
     * 查询指定类目下的商品
     *
     * @param categoryId 类目ID
     * @return 商品列表
     */
    @Select("SELECT * FROM t_product WHERE category_id = #{categoryId} AND status = 1")
    List<Product> selectByCategoryId(@Param("categoryId") Long categoryId);

    /**
     * 更新商品库存
     *
     * @param productId 商品ID
     * @param stock 库存数量
     * @return 更新结果
     */
    @Update("UPDATE t_product SET stock = stock + #{stock} WHERE id = #{productId} AND stock + #{stock} >= 0")
    int updateStock(@Param("productId") Long productId, @Param("stock") Integer stock);

    /**
     * 扣减商品库存（乐观锁）
     *
     * @param productId 商品ID
     * @param quantity 扣减数量
     * @param version 版本号
     * @return 更新结果
     */
    @Update("UPDATE t_product SET stock = stock - #{quantity}, version = version + 1 " +
            "WHERE id = #{productId} AND version = #{version} AND stock >= #{quantity}")
    int deductStock(@Param("productId") Long productId, 
                    @Param("quantity") Integer quantity, 
                    @Param("version") Integer version);

    /**
     * 搜索商品
     *
     * @param keyword 关键词
     * @param page 分页参数
     * @return 商品分页数据
     */
    @Select("SELECT * FROM t_product WHERE status = 1 AND " +
            "(name LIKE CONCAT('%', #{keyword}, '%') OR " +
            "title LIKE CONCAT('%', #{keyword}, '%') OR " +
            "detail LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY create_time DESC")
    IPage<Product> searchProducts(@Param("keyword") String keyword, Page<Product> page);
}