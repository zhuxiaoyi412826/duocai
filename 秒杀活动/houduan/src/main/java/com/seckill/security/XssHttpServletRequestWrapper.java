package com.seckill.security;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.*;
import java.util.regex.Pattern;

/**
 * XSS过滤处理包装类
 */
public class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

    // 排除的参数名列表
    private List<String> excludeParams;

    // HTML过滤配置
    private static final String[] EMPTY_ARRAY = new String[0];
    private static final Pattern SCRIPT_PATTERN = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_PATTERN = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SRC_PATTERN_2 = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern SCRIPT_END_PATTERN = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVAL_PATTERN = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern EXPRESSION_PATTERN = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern VBSCRIPT_PATTERN = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
    private static final Pattern ONLOAD_PATTERN = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    public XssHttpServletRequestWrapper(HttpServletRequest request, List<String> excludeParams) {
        super(request);
        this.excludeParams = excludeParams;
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return isExcludeParam(name) ? value : cleanXSS(value);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return EMPTY_ARRAY;
        }

        if (isExcludeParam(name)) {
            return values;
        }

        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = cleanXSS(values[i]);
        }
        return encodedValues;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new HashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (String key : parameters.keySet()) {
            String[] values = parameters.get(key);
            if (values == null) {
                continue;
            }

            if (isExcludeParam(key)) {
                map.put(key, values);
                continue;
            }

            int count = values.length;
            String[] encodedValues = new String[count];
            for (int i = 0; i < count; i++) {
                encodedValues[i] = cleanXSS(values[i]);
            }
            map.put(key, encodedValues);
        }
        return map;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return cleanXSS(value);
    }

    /**
     * 判断是否为排除的参数
     */
    private boolean isExcludeParam(String name) {
        return excludeParams != null && excludeParams.contains(name);
    }

    /**
     * 清除XSS字符
     */
    private String cleanXSS(String value) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }

        // 使用ESAPI库来防止XSS攻击
        value = StringEscapeUtils.escapeHtml4(value);

        // 避免script标签
        value = SCRIPT_PATTERN.matcher(value).replaceAll("");

        // 避免src形式的表达式
        value = SRC_PATTERN.matcher(value).replaceAll("");
        value = SRC_PATTERN_2.matcher(value).replaceAll("");

        // 删除单个的 </script> 标签
        value = SCRIPT_END_PATTERN.matcher(value).replaceAll("");

        // 删除eval(...) 表达式
        value = EVAL_PATTERN.matcher(value).replaceAll("");

        // 删除expression(...) 表达式
        value = EXPRESSION_PATTERN.matcher(value).replaceAll("");

        // 避免javascript: 表达式
        value = JAVASCRIPT_PATTERN.matcher(value).replaceAll("");

        // 避免vbscript: 表达式
        value = VBSCRIPT_PATTERN.matcher(value).replaceAll("");

        // 避免onload= 表达式
        value = ONLOAD_PATTERN.matcher(value).replaceAll("");

        return value;
    }
}