package com.seckill.security;

import org.apache.commons.lang3.StringUtils;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * XSS过滤器
 */
public class XssFilter implements Filter {
    
    // 排除链接
    private List<String> excludes = new ArrayList<>();
    
    // 排除参数
    private List<String> excludeParams = new ArrayList<>();

    @Override
    public void init(FilterConfig filterConfig) {
        String tempExcludes = filterConfig.getInitParameter("excludes");
        String tempExcludeParams = filterConfig.getInitParameter("excludeParams");
        
        if (StringUtils.isNotEmpty(tempExcludes)) {
            String[] url = tempExcludes.split(",");
            for (int i = 0; url != null && i < url.length; i++) {
                excludes.add(url[i].trim());
            }
        }
        
        if (StringUtils.isNotEmpty(tempExcludeParams)) {
            String[] params = tempExcludeParams.split(",");
            for (int i = 0; params != null && i < params.length; i++) {
                excludeParams.add(params[i].trim());
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 如果是排除的URL，则直接放行
        if (handleExcludeURL(httpRequest)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 使用XSS过滤包装器
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(httpRequest, excludeParams);
        chain.doFilter(xssRequest, response);
    }

    @Override
    public void destroy() {
        // 销毁时的清理工作
    }

    /**
     * 判断是否为排除的URL
     */
    private boolean handleExcludeURL(HttpServletRequest request) {
        if (excludes == null || excludes.isEmpty()) {
            return false;
        }
        
        String url = request.getServletPath();
        for (String pattern : excludes) {
            Pattern p = Pattern.compile("^" + pattern);
            Matcher m = p.matcher(url);
            if (m.find()) {
                return true;
            }
        }
        return false;
    }
}