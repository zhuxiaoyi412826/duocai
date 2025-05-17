package com.duocai.mall.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * 物流实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Shippings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Orders orders;
    
    @Column(name = "shipping_no", nullable = false, unique = true)
    private String shippingNo;
    
    private String company;
    private String status;
    private String receiver;
    private String phone;
    private String address;
    
    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    // Getters and Setters
}