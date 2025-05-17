package com.duocai.mall.mapper;

import com.duocai.mall.model.Products;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * 商品数据访问接口
 * @author trae
 */
@Mapper
public interface ProductMapper {
    /**
     * 插入新商品
     * @param product 商品对象
     */
    @Insert({"<script>",
        "INSERT INTO product (name, description, price, stock, status, created_at, updated_at)",
        "VALUES (#{product.name}, #{product.description}, #{product.price}, #{product.stock},", 
        "#{product.status}, #{product.createdAt}, #{product.updatedAt})",
        "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "product.id")
    void insert(@Param("product") Products product);
    
    /**
     * 根据ID更新商品
     * @param product 商品对象
     * @return 更新的记录数
     */
    @Update({"<script>",
        "UPDATE product SET name = #{product.name}, description = #{product.description},", 
        "price = #{product.price}, stock = #{product.stock}, status = #{product.status},", 
        "updated_at = #{product.updatedAt}",
        "WHERE id = #{product.id}",
        "</script>"})
    int update(@Param("product") Products product);
    
    /**
     * 根据ID查询商品
     * @param id 商品ID
     * @return 商品对象
     */
    @Select("SELECT * FROM product WHERE id = #{id}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "name", column = "name"),
        @Result(property = "description", column = "description"),
        @Result(property = "price", column = "price"),
        @Result(property = "stock", column = "stock"),
        @Result(property = "status", column = "status"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Products selectById(Long id);
    
    /**
     * 查询所有商品
     * @param categoryId 分类ID
     * @param keyword 搜索关键词
     * @param offset 分页偏移量
     * @param size 分页大小
     * @return 商品列表
     */
    @Select({"<script>",
        "SELECT * FROM product WHERE 1=1",
        "<if test='categoryId != null'> AND category_id = #{categoryId} </if>",
        "<if test='keyword != null and keyword != '''> AND (name LIKE CONCAT('%', #{keyword}, '%') OR description LIKE CONCAT('%', #{keyword}, '%')) </if>",
        "LIMIT #{offset}, #{size}",
        "</script>"})
    List<Products> selectProducts(@Param("categoryId") Long categoryId,
                                 @Param("keyword") String keyword,
                                 @Param("offset") int offset,
                                 @Param("size") int size);
    
    /**
     * 根据ID删除商品
     * @param id 商品ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM product WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新商品库存
     * @param id 商品ID
     * @param quantity 库存变化量
     * @return 更新的记录数
     */
    @Update({"<script>",
        "UPDATE product SET stock = stock + #{quantity}, updated_at = NOW()",
        "WHERE id = #{id}",
        "</script>"})
    int updateStock(@Param("id") Long id, @Param("quantity") Integer quantity);
    
    /**
     * 更新商品状态
     * @param id 商品ID
     * @param status 新状态
     * @return 更新的记录数
     */
    @Update({"<script>",
        "UPDATE product SET status = #{status}, updated_at = NOW()",
        "WHERE id = #{id}",
        "</script>"})
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}