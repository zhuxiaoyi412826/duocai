package com.duocai.mall.mapper;

import com.duocai.mall.model.CartItems;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.*;

/**
 * 购物车数据访问接口
 * @author trae
 */
@Mapper
public interface CartMapper {
    /**
     * 根据用户ID和商品ID查询购物车项
     * @param userId 用户ID
     * @param productId 商品ID
     * @return 购物车项
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND product_id = #{productId}")
    CartItems selectByUserIdAndProductId(Long userId, Long productId);
    
    /**
     * 根据购物车项ID查询
     * @param id 购物车项ID
     * @return 购物车项
     */
    @Select("SELECT * FROM cart WHERE id = #{id}")
    CartItems selectById(Long id);
    
    /**
     * 查询用户的所有购物车项
     * @param userId 用户ID
     * @return 购物车项列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId}")
    List<CartItems> selectByUserId(Long userId);
    
    /**
     * 插入新的购物车项
     * @param cartItem 购物车项
     * @return 影响的行数
     */
    @Insert("INSERT INTO cart (user_id, product_id, product_name, product_image, product_price, quantity, selected, created_at, updated_at) VALUES (#{userId}, #{productId}, #{productName}, #{productImage}, #{productPrice}, #{quantity}, #{selected}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CartItems cartItem);
    
    /**
     * 更新购物车项
     * @param cartItem 购物车项
     * @return 影响的行数
     */
    @Update("UPDATE cart SET quantity = #{quantity}, selected = #{selected}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(CartItems cartItem);
    
    /**
     * 根据ID和用户ID删除购物车项
     * @param id 购物车项ID
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM cart WHERE id = #{id} AND user_id = #{userId}")
    int deleteByIdAndUserId(Long id, Long userId);
    
    /**
     * 根据用户ID删除所有购物车项
     * @param userId 用户ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM cart WHERE user_id = #{userId}")
    int deleteByUserId(Long userId);
    
    /**
     * 查询选中的购物车项
     * @param userId 用户ID
     * @return 选中的购物车项列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND selected = 1")
    List<CartItems> selectSelectedByUserId(Long userId);

    /**
     * 更新购物车项的选中状态
     * @param id 购物车项ID
     * @param userId 用户ID
     * @param selected 选中状态（1表示选中，0表示未选中）
     * @return 受影响的行数
     */
    @Update("UPDATE cart SET selected = #{selected} WHERE id = #{id} AND user_id = #{userId}")
    int updateSelectedStatus(@Param("id") Long id, @Param("userId") Long userId, @Param("selected") Integer selected);

    /**
     * 根据用户ID查询选中的购物车项
     * @param userId 用户ID
     * @return 选中的购物车项列表
     */
    @Select("SELECT * FROM cart WHERE user_id = #{userId} AND selected = 1")
    List<CartItems> selectSelectedItems(Long userId);

    /**
     * 根据ID列表查询购物车项
     * @param ids ID列表
     * @return 购物车项列表
     */
    @Select({"<script>",
      "SELECT * FROM cart WHERE id IN",
      "<foreach collection='ids' item='id' open='(' separator=',' close=')'>",
      "#{id}",
      "</foreach>",
      "</script>"})
    List<CartItems> selectByIds(List<Long> ids);
}