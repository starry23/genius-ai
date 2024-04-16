package cn.apeto.geniusai.server.config;

import cn.apeto.geniusai.sdk.function.AccessKeyHandler;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.utils.BaiduUtils;
import cn.apeto.geniusai.server.utils.CommonUtils;
import org.springframework.stereotype.Component;

/**
 * @author apeto
 * @create 2023/7/20 11:47 下午
 */
@Component
public class BaiduAccessToken implements AccessKeyHandler {

    @Override
    public String getAccessKey() {

        ChatConfigEntity chatConfig = CommonUtils.getChatConfig();
        ChatConfigEntity.WXQFEntity wxqfEntity = chatConfig.getWxqfEntity();
        String appId = wxqfEntity.getAppId();
        String clientId = wxqfEntity.getClient_id();
        String clientSecret = wxqfEntity.getClient_secret();
        return BaiduUtils.getBaiduAccessToken(appId,clientId,clientSecret);
    }
}
