package com.lunarmeal.prisonescape.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderFormat {
    private final Pattern pattern;
    private final String template;

    public PlaceholderFormat(String template) {
        this.template = template;
        this.pattern = Pattern.compile("\\{([a-zA-Z0-9_]+)\\}"); // 匹配形如 {xxx} 的占位符
    }


    /**
     * 根据给定的参数值格式化字符串并返回结果。
     *
     * @param args 参数值
     * @return 格式化后的字符串
     */
    public String format(Object... args) {
        Matcher matcher = pattern.matcher(template);
        StringBuffer sb = new StringBuffer();
        int argIndex = 0;

        while (matcher.find()) {
            String argName = matcher.group(1);
            Object argValue = getArgumentValue(args, argIndex);
            argIndex++;

            matcher.appendReplacement(sb, Matcher.quoteReplacement(argValue != null ? argValue.toString() : ""));
        }

        matcher.appendTail(sb);

        return sb.toString();
    }

    private Object getArgumentValue(Object[] args, int index) {
        if (index >= 0 && index < args.length) {
            return args[index];
        }
        return null;
    }

}
