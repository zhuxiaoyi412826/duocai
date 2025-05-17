package com.duocai.mall.mapper;

import com.duocai.mall.model.Categories;
import org.apache.ibatis.annotations.*;
import java.util.List;

/**
 * 商品分类数据访问接口
 * @author trae
 */
@Mapper
public interface CategoryMapper {
    /**
     * 根据ID查询分类
     * @param id 分类ID
     * @return 分类对象
     */
    @Select("SELECT * FROM category WHERE id = #{id}")
    Categories selectById(Long id);
    
    /**
     * 查询所有分类
     * @return 分类列表
     */
    @Select("SELECT * FROM category")
    List<Categories> selectAll();
    
    /**
     * 根据父ID查询分类
     * @param parentId 父分类ID
     * @return 分类列表
     */
    @Select("SELECT * FROM category WHERE parent_id = #{parentId}")
    List<Categories> selectByParentId(Long parentId);
    
    /**
     * 插入分类
     * @param category 分类对象
     * @return 影响行数
     */
    @Insert({"<script>",
      "INSERT INTO category (name, parent_id, level, status, created_at, updated_at)",
      "VALUES (#{name}, #{parentId}, #{level}, #{status}, #{createdAt}, #{updatedAt})",
      "</script>"})
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Categories category);
    
    /**
     * 更新分类
     * @param category 分类对象
     * @return 影响行数
     */
    @Update({"<script>",
      "UPDATE category SET name = #{name}, parent_id = #{parentId}, level = #{level},", 
      "status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}",
      "</script>"})
    int update(Categories category);
    
    /**
     * 删除分类
     * @param id 分类ID
     * @return 影响行数
     */
    @Delete("DELETE FROM category WHERE id = #{id}")
    int deleteById(Long id);
    
    /**
     * 更新分类状态
     * @param id 分类ID
     * @param status 新状态
     * @return 影响行数
     */
    @Update("UPDATE category SET status = #{status} WHERE id = #{id}")
    int updateStatus(Long id, Integer status);
}