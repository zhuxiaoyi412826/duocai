package com.seckill.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * IP地址工具类
 */
@Slf4j
public class IpUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    /**
     * 获取客户端IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            // X-Forwarded-For：Squid 服务代理
            ipAddress = request.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // Proxy-Client-IP：apache 服务代理
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // WL-Proxy-Client-IP：weblogic 服务代理
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // HTTP_CLIENT_IP：有些代理服务器
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // X-Real-IP：nginx服务代理
                ipAddress = request.getHeader("X-Real-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                // 获取真实IP地址
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    // 根据网卡取本机配置的IP
                    try {
                        InetAddress inet = InetAddress.getLocalHost();
                        ipAddress = inet.getHostAddress();
                    } catch (UnknownHostException e) {
                        log.error("获取IP地址异常：", e);
                    }
                }
            }

            // 对于通过多个代理的情况，第一个IP为客户端真实IP，多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(SEPARATOR));
                }
            }
        } catch (Exception e) {
            log.error("获取IP地址异常：", e);
            ipAddress = "";
        }

        return ipAddress;
    }

    /**
     * 判断IP地址是否为内网IP
     */
    public static boolean isInnerIp(String ipAddress) {
        if (!StringUtils.hasLength(ipAddress)) {
            return false;
        }

        String[] ipParts = ipAddress.split("\\.");
        if (ipParts.length != 4) {
            return false;
        }

        try {
            // 10.0.0.0 - 10.255.255.255
            // 172.16.0.0 - 172.31.255.255
            // 192.168.0.0 - 192.168.255.255
            int firstPart = Integer.parseInt(ipParts[0]);
            int secondPart = Integer.parseInt(ipParts[1]);

            return firstPart == 10 ||
                    (firstPart == 172 && secondPart >= 16 && secondPart <= 31) ||
                    (firstPart == 192 && secondPart == 168);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 检查IP地址格式是否正确
     */
    public static boolean isValidIp(String ipAddress) {
        if (!StringUtils.hasLength(ipAddress)) {
            return false;
        }

        String[] ipParts = ipAddress.split("\\.");
        if (ipParts.length != 4) {
            return false;
        }

        try {
            for (String part : ipParts) {
                int value = Integer.parseInt(part);
                if (value < 0 || value > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 将IP地址转换为long类型
     */
    public static long ipToLong(String ipAddress) {
        if (!isValidIp(ipAddress)) {
            throw new IllegalArgumentException("Invalid IP address");
        }

        String[] ipParts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = result << 8 | Integer.parseInt(ipParts[i]);
        }
        return result;
    }

    /**
     * 将long类型转换为IP地址
     */
    public static String longToIp(long ipLong) {
        StringBuilder sb = new StringBuilder();
        sb.append((ipLong >> 24) & 0xFF).append(".");
        sb.append((ipLong >> 16) & 0xFF).append(".");
        sb.append((ipLong >> 8) & 0xFF).append(".");
        sb.append(ipLong & 0xFF);
        return sb.toString();
    }
}