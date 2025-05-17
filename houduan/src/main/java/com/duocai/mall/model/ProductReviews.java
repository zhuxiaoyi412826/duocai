package com.duocai.mall.model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * 商品评论实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductReviews {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Products product;
    
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Orders order;
    
    private String content;
    private Integer rating;
    
    @Column(columnDefinition = "json")
    private String images;
    
    private Boolean isAnonymous;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private Boolean isDeleted;
    
    // Getters and Setters
}