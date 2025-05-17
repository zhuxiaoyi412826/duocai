package com.duocai.mall.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence .*;

/**
 * 商品分类实体类
 * @author trae
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Column(name = "parent_id")
    // 使用JPA注解@Column指定列名
    private Long parentId;
    private Integer level;
    private Integer sort;
    private Integer status; // 1: 启用, 0: 禁用
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // Lombok自动生成getter/setter、toString、equals、hashCode
    // 可根据需要补充无参构造方法
}