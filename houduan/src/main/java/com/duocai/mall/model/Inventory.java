package com.duocai.mall.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * 库存实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Products product;
    
    @ManyToOne
    @JoinColumn(name = "spec_id", nullable = false)
    private ProductSpecs spec;
    
    private Integer quantity;
    
    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;
    
    @Column(name = "low_stock", nullable = false)
    private Integer lowStock;
    
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false, updatable = false)
    private LocalDateTime updatedAt;
    
    // Getters and Setters
}