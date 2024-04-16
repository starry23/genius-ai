package cn.apeto.geniusai.server.utils;

import cn.hutool.http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

import static cn.apeto.geniusai.server.core.mj.ApetoMjServiceImpl.AI_IMAGE_DOMAIN;

/**
 * @author apeto
 * @create 2023/7/8 12:56 上午
 */
public class ApetoMjUtils {

    static String MJ_TENANT_USER_INFO = AI_IMAGE_DOMAIN + "/app-api/aigc/mj/tenantInfo";

    public static String tenantInfo(String apiKey, Integer tenantId) {
        Map<String, Object> map = new HashMap<>();
        map.put("apiKey", apiKey);
        return HttpRequest.get(MJ_TENANT_USER_INFO).form(map).headerMap(headerMap(tenantId, apiKey), true).execute().body();
    }

    public static Map<String, String> headerMap(Integer tenantId, String apiKey) {
        Map<String, String> map = new HashMap<>();
        map.put("tenant-id", String.valueOf(tenantId));
        map.put("api-key", apiKey);
        return map;
    }
}
