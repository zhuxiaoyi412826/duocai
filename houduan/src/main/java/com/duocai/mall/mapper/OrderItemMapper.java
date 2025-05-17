package com.duocai.mall.mapper;

import com.duocai.mall.model.OrderItems;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 订单项数据访问接口
 * @author trae
 */
@Mapper
public interface OrderItemMapper {
    /**
     * 插入订单项
     * @param orderItem 订单项信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO order_items (order_id, product_id, product_name, product_image, product_price, " +
            "quantity, total_price, created_at, updated_at) " +
            "VALUES (#{orderId}, #{productId}, #{productName}, #{productImage}, #{productPrice}, " +
            "#{quantity}, #{totalPrice}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OrderItems orderItem);
    
    /**
     * 根据订单ID查询订单项列表
     * @param orderId 订单ID
     * @return 订单项列表
     */
    @Select("SELECT * FROM order_items WHERE order_id = #{orderId}")
    List<OrderItems> selectByOrderId(Long orderId);
    
    /**
     * 根据订单ID删除订单项
     * @param orderId 订单ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM order_items WHERE order_id = #{orderId}")
    int deleteByOrderId(Long orderId);
}