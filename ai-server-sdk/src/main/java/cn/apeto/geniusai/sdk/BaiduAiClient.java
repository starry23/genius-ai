package cn.apeto.geniusai.sdk;

import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletion;
import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletionResponse;
import cn.apeto.geniusai.sdk.entity.weixin.ResponseToken;

/**
 * @author wanmingyu
 * @create 2023/7/20 8:51 下午
 */
public abstract class BaiduAiClient {

    protected BaiduWenXinAiApi baiduWenXinAiApi;

    /**
     * 获取token
     *
     * @param client_id
     * @param client_secret
     * @return
     */
    public ResponseToken getAccessToken(String client_id, String client_secret) {
        return baiduWenXinAiApi.getToken("client_credentials", client_id, client_secret).blockingGet();
    }

    /**
     * 获取token
     *
     * @param client_id
     * @param client_secret
     * @return
     */
    public ResponseToken getAccessToken(String grant_type, String client_id, String client_secret) {
        return baiduWenXinAiApi.getToken(grant_type, client_id, client_secret).blockingGet();
    }

    public BaiduCompletionResponse completions(BaiduCompletion baiduCompletion){
        return baiduWenXinAiApi.completions(baiduCompletion).blockingGet();
    }

}
