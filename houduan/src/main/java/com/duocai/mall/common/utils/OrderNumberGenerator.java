package com.duocai.mall.common.utils;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 订单号生成器
 * @author trae
 */
@Component
public class OrderNumberGenerator {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
    private static final int MAX_SEQUENCE = 9999;

    /**
     * 生成订单号
     * 格式：年月日时分秒 + 4位序列号
     * @return 订单号
     */
    public String generate() {
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DATE_FORMATTER);
        
        int sequence = SEQUENCE.incrementAndGet();
        if (sequence > MAX_SEQUENCE) {
            synchronized (SEQUENCE) {
                if (SEQUENCE.get() > MAX_SEQUENCE) {
                    SEQUENCE.set(0);
                }
                sequence = SEQUENCE.incrementAndGet();
            }
        }
        
        return String.format("%s%04d", timestamp, sequence);
    }
}