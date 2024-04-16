package cn.apeto.geniusai.server.utils;

import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.PromptTemplate;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author apeto
 * @create 2023/8/3 11:49
 */
@Slf4j
public class SerpapiUtils {

    private static final String BASE_URL = "https://serpapi.com/search";

    public static List<String> searchBaiduSnippets(String query, String model, Integer surplusToken) {

        try {
            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            String searchApiApiKey = packageWebConfig.getSearchApiApiKey();
            if (StrUtil.isBlank(searchApiApiKey)) {
                log.error("searchApiKey 没有填写");
                return null;
            }
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("q", query);
            paramMap.put("num", 5);
            paramMap.put("api_key", searchApiApiKey);
            paramMap.put("engine", "bing");
            String res = HttpUtil.get(BASE_URL, paramMap);
            JSONObject resObj = JSONUtil.parseObj(res);
            List<String> snippets = new ArrayList<>();
            JSONArray organic_results = resObj.getJSONArray("organic_results");

            Integer currentToken = 0;
            for (Object organicResult : organic_results) {
                JSONObject obj = JSONUtil.parseObj(organicResult);
                String snippet = obj.getStr("snippet");
                if (StrUtil.isBlank(snippet)) {
                    continue;
                }

                if (currentToken > surplusToken || currentToken > 5000) {
                    break;
                }
                currentToken += TikTokensUtil.tokens(model, snippet);
                snippets.add(snippet);

            }
            return snippets;
        } catch (Exception e) {
            log.error("联网异常", e);
        }
        return null;
    }

    public static MyMessage createMessageSearchBaiduSnippets(String query, String model, Integer surplusToken) {

        List<String> list = searchBaiduSnippets(query,model,surplusToken);
        if (list == null) {
            return null;
        }
        String result = String.join("\n", list);
        String sysMessage = StrFormatter.formatWith(PromptTemplate.LOOSE, "{text_content}", result);
        return new MyMessage(Message.Role.SYSTEM.getName(), sysMessage);

    }


}
