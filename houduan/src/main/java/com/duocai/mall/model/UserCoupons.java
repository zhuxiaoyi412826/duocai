package com.duocai.mall.model;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * 用户优惠券实体类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupons {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users users;
    
    @ManyToOne
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupons coupon;
    
    @ManyToOne
    @JoinColumn(name = "orders_id")
    private Orders orders;
    
    private String status;
    
    @Column(name = "get_time", nullable = false)
    private LocalDateTime getTime;
    
    @Column(name = "use_time", nullable = false)
    private LocalDateTime useTime;
    
    // Getters and Setters
}