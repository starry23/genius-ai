package cn.apeto.geniusai.server.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO.BaiduSetting;
import cn.apeto.geniusai.server.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author apeto
 * @create 2023/6/1 11:57
 */
@Slf4j
public class BaiduUtils {


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    public static String getBaiduAccessToken(String appId, String client_id, String client_secret) {


        String accessTokenKey = SystemConstants.RedisKeyEnum.BAIDU_ACCESS_TOKEN.getKey(appId);
        String access_token = StringRedisUtils.get(accessTokenKey);
        if (StrUtil.isNotEmpty(access_token)) {
            return access_token;
        }

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "client_credentials");
        paramMap.put("client_id", client_id);
        paramMap.put("client_secret", client_secret);
        log.info("获取百度accessToken请求参数：{}", paramMap);
        String res = HttpUtil.post("https://aip.baidubce.com/oauth/2.0/token", paramMap);
        log.info("获取百度accessToken返回结果：{}", res);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        access_token = jsonObject.getStr("access_token");
        Integer expiresIn = jsonObject.getInt("expires_in");

        StringRedisUtils.setForTimeSE(accessTokenKey, access_token, expiresIn - 2);
        return access_token;
    }


    /**
     * 重要提示代码中所需工具类
     * FileUtil,Base64Util,HttpUtil,GsonUtils请从
     * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
     * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
     * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
     * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
     * 下载
     */
    public static boolean baiduTextCensor(String text) {

        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        BaiduSetting baiduSetting = packageWebConfig.getBaiduSetting();
        if (baiduSetting == null) {
            throw new ServiceException(4001, "百度API信息没有配置!");
        }

        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined" + "?access_token=" + getBaiduAccessToken(baiduSetting.getAppId(),baiduSetting.getAppKey(),baiduSetting.getSecretKey());
        try {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("text", text);
            String result = HttpUtil.post(url, paramMap);
            if (StrUtil.isNotEmpty(result)) {
                JSONObject jsonObject = JSONUtil.parseObj(result);
                Integer conclusionType = jsonObject.getInt("conclusionType");
                return conclusionType != null && conclusionType == 1;
            }
        } catch (Exception e) {
            log.error("baiduTextCensor 请求异常", e);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("user.dir"));
    }

}
