package com.duocai.mall.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import jakarta.persistence.*;

/**
 * 操作日志实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
    
    private String module;
    private String operation;
    private String method;
    
    @Column(columnDefinition = "text")
    private String params;
    
    private String ip;
    
    @Column(name = "user_agent", length = 500)
    private String userAgent;
    
    private Integer status;
    
    @Column(name = "error_msg", columnDefinition = "text")
    private String errorMsg;
    
    @Column(name = "operation_time", nullable = false)
    private LocalDateTime operationTime;
    
    // Getters and Setters
}