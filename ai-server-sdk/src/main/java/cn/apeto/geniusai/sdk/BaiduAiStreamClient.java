package cn.apeto.geniusai.sdk;

import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletion;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.sdk.interceptor.OpenAILogger;
import cn.apeto.geniusai.sdk.interceptor.RequestParamInterceptor;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wanmingyu
 * @create 2023/7/20 8:50 下午
 */
@Slf4j
public class BaiduAiStreamClient extends BaiduAiClient {

    private final OkHttpClient okHttpClient;

    public void streamChatCompletion(BaiduCompletion baiduCompletion, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空，可以参考：com.unfbx.chatgpt.sse.ConsoleEventSourceListener");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        if (baiduCompletion.getStream() == null) {
            baiduCompletion.setStream(true);
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(baiduCompletion);
            Request request = new Request.Builder()
                    .url(BaiduWenXinAiApi.BASE_URL + BaiduWenXinAiApi.COMPLETIONS_URL)
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常", e);
        }
    }


    /**
     * 构造器
     */
    public static Builder builder() {
        return new Builder();
    }

    public BaiduAiStreamClient(Builder builder) {
        OkHttpClient okHttpClient = builder.okHttpClient;
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        RequestParamInterceptor requestParamInterceptor = builder.requestParamInterceptor;
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient
                    .Builder()
                    .addInterceptor(requestParamInterceptor).addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS).build();
        } else {
            if (CollUtil.isEmpty(okHttpClient.interceptors())) {
                okHttpClient = okHttpClient.newBuilder()
                        .addInterceptor(requestParamInterceptor).addInterceptor(httpLoggingInterceptor)
                        .build();
            }
        }
        this.okHttpClient = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JacksonConverterFactory jacksonConverterFactory = JacksonConverterFactory.create(mapper);
        super.baiduWenXinAiApi = new Retrofit.Builder()
                .baseUrl(BaiduWenXinAiApi.BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(jacksonConverterFactory)
                .build().create(BaiduWenXinAiApi.class);

    }

    public static final class Builder {

        public Builder() {
        }

        private OkHttpClient okHttpClient;

        private RequestParamInterceptor requestParamInterceptor;

        public Builder paramInterceptor(RequestParamInterceptor val) {
            requestParamInterceptor = val;
            return this;
        }

        public Builder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public BaiduAiStreamClient build() {
            return new BaiduAiStreamClient(this);
        }
    }

}
