package cn.apeto.geniusai.sdk.interceptor;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.apeto.geniusai.sdk.entity.KeyInfo;
import cn.apeto.geniusai.sdk.entity.chat.BaseReq;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.images.Image;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.sdk.function.KeyStrategy;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;

import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class OpenAiAuthInterceptor implements Interceptor {

    public OpenAiAuthInterceptor(KeyStrategy keyStrategy) {
        this.keyStrategy = keyStrategy;
    }


    protected KeyStrategy keyStrategy;

    protected void onErrorDealApiKeys(String key, String desc) {
        keyStrategy.removeErrorKey(key, desc);
    }

    // 将RequestBody转换为字符串的方法
    protected static String requestBodyToString(RequestBody requestBody) {
        try {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);
            return buffer.readUtf8();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取请求key
     *
     * @return key
     */
    public final KeyInfo getKeyInfo(Request original) {


        List<KeyInfo> keys = keyStrategy.getKeys(null, null);
        if (CollectionUtil.isEmpty(keys)) {
            this.keyStrategy.keysWarring();
            throw new BaseException(CommonError.NO_ACTIVE_API_KEYS);
        }
        RequestBody body = original.body();
        String method = original.method();

        if (!"POST".equals(method) || body == null) {
            return keyStrategy.strategy(null, null);
        }
        MediaType mediaType = body.contentType();
        if (mediaType == null) {
            return keyStrategy.strategy(null, null);
        }
        String subtype = mediaType.subtype();
        String type = mediaType.type();

        if (!"application".equals(type) || !"json".equals(subtype)) {
            return keyStrategy.strategy(null, null);
        }
        String requestBody = requestBodyToString(body);
        if (StrUtil.isBlank(requestBody) || !JSONUtil.isTypeJSON(requestBody)) {
            return keyStrategy.strategy(null, null);

        }
        JSONObject jsonObject = JSONUtil.parseObj(requestBody);
        String modelType = jsonObject.getStr("modelType");
        ChatCompletion.ModelType modelTypeEnum = ChatCompletion.ModelType.getByName(modelType);
        if (ChatCompletion.ModelType.DALL_E.equals(modelTypeEnum)) {
            Image image = JSONUtil.toBean(requestBody, Image.class);
            return filterModelType(image);
        }
        if (ChatCompletion.ModelType.GPT3
                .equals(modelTypeEnum) || ChatCompletion.ModelType.GPT4
                .equals(modelTypeEnum) || ChatCompletion.ModelType.GPTS
                .equals(modelTypeEnum)) {
            ChatCompletion chatCompletion =  JSONUtil.toBean(requestBody, ChatCompletion.class);
            return filterModelType(chatCompletion);
        }
        return keyStrategy.strategy(null, null);
    }

    private KeyInfo filterModelType(BaseReq baseReq) {

        if (baseReq == null) {
            return keyStrategy.strategy(null, null);
        }

        ChatCompletion.ModelType modelTypeEnum = baseReq.getModelType();
        String model = baseReq.getModel();
        KeyInfo strategy = keyStrategy.strategy(modelTypeEnum, model);

        try {
            ObjectMapper mapper = new ObjectMapper();
            baseReq.setModelType(null);
            String content = mapper.writeValueAsString(baseReq);
            RequestBody newBody = RequestBody.create(MediaType.parse("application/json"), content);
            strategy.setRequestBody(newBody);
        } catch (Exception e) {
            log.info("getKeyInfo writeValueAsString 异常", e);
        }
        return strategy;
    }

    /**
     * 默认的鉴权处理方法
     *
     * @param original 源请求体
     * @return 请求体
     */
    public AuthInfo auth(Request original) {
        KeyInfo keyInfo = getKeyInfo(original);
        String key = keyInfo.getKey();
        String apiHost = keyInfo.getApiHost();
        RequestBody body = Optional.ofNullable(keyInfo.getRequestBody()).orElse(original.body());
        Request request = original.newBuilder()
                .url(StrUtil.isBlank(apiHost) ? original.url() : original.url().newBuilder().host(apiHost).build())
                .header(Header.AUTHORIZATION.getValue(), "Bearer " + key)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), body)
                .build();
        AuthInfo authInfo = new AuthInfo();
        authInfo.setKey(key);
        authInfo.setRequest(request);
        authInfo.setRequestBody(body);
        return authInfo;
    }

    @Data
    public static class AuthInfo {
        private String key;
        private Request request;
        private RequestBody requestBody;

    }
}
