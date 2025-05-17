package com.duocai.mall.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 图片实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Images {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String url;
    private String path;
    private Integer type;
    private Long relatedId;
    private String name;
    private Integer size;
    private Integer width;
    private Integer height;
    private String format;
    private String hash;
    private Boolean isMain;
    private Integer sortOrder;
    private Boolean status;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    private Long createdBy;
    private Long updatedBy;
    
    // Getters and Setters
}