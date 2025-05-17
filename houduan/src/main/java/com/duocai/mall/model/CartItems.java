package com.duocai.mall.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 购物车项实体类
 * 移除JPA注解，使用纯POJO方式，避免与MyBatis冲突
 * @author trae
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItems {
    private Long id;
    
    // 直接使用ID字段，而不是关联对象
    private Long userId;
    private Long productId;
    
    private String productName;
    private String productImage;
    private Double productPrice;
    private Long specId;
    private Integer quantity;
    private Boolean selected;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}