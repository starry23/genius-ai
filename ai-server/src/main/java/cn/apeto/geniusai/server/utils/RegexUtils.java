package cn.apeto.geniusai.server.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author apeto
 * @create 2023/7/30 11:24 上午
 */
public class RegexUtils {

    /**
     * 预编译
     */
    private static final Pattern SPECIAL_CHARACTERS_PATTERN = Pattern.compile("[\\t\\n\\r ]|\\.{4}");

    public static boolean isHttpOrHttps(String urlString) {
        String regex = "^(http|https)://.*$";
        Pattern pattern = Pattern.compile(regex);

        return pattern.matcher(urlString).matches();
    }

    public static String filterSpecialCharacters(String text) {
        Matcher matcher = SPECIAL_CHARACTERS_PATTERN.matcher(text);
        String filteredText = matcher.replaceAll("");
        return filteredText.trim();
    }

    /**
     * 大数据量用此方法 较快
     * @param text
     * @return
     */
    public static String bigDataFilterSpecialCharacters(String text) {
        StringBuilder sb = new StringBuilder();
        int pointCount = 0;  // 用于跟踪连续点的数量

        for (char c : text.toCharArray()) {
            switch (c) {
                case ' ':
                case '\t':
                case '\n':
                case '\r':
                    // 跳过这些字符
                    break;
                case '.':
                    pointCount++;
                    if (pointCount < 4) {
                        sb.append(c);
                    }
                    break;
                default:
                    pointCount = 0;  // 重置连续点的数量
                    sb.append(c);
            }
        }

        return sb.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println(filterSpecialCharacters("\t sadsa  asda ..............."));
    }
}
