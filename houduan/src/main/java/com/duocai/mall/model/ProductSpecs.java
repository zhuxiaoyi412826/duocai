package com.duocai.mall.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * 商品规格实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSpecs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;
    
    private String name;
    private String value;
    
    @Column(name = "price_diff", precision = 10, scale = 2)
    private BigDecimal priceDiff;
    
    private Integer stock;
    private String image;
    
    // Getters and Setters
}