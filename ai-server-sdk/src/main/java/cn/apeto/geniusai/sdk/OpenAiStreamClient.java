package cn.apeto.geniusai.sdk;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.apeto.geniusai.sdk.constant.OpenAIConst;
import cn.apeto.geniusai.sdk.entity.KeyInfo;
import cn.apeto.geniusai.sdk.entity.billing.BillingUsage;
import cn.apeto.geniusai.sdk.entity.billing.Subscription;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.sdk.function.KeyRandomStrategy;
import cn.apeto.geniusai.sdk.function.KeyStrategy;
import cn.apeto.geniusai.sdk.interceptor.DefaultOpenAiAuthInterceptor;
import cn.apeto.geniusai.sdk.interceptor.OpenAiAuthInterceptor;
import cn.apeto.geniusai.sdk.sse.ConsoleEventSourceListener;
import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.jetbrains.annotations.NotNull;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 描述： open ai 客户端
 *
 * @author https:www.unfbx.com 2023-02-28
 */

@Slf4j
public class OpenAiStreamClient extends OpenAiClient {


    /**
     * 构造器
     *
     */
    public static Builder builder() {
        return new Builder();
    }


    /**
     * 构造
     *
     */
    public OpenAiStreamClient(Builder builder) {
        String openaiHost = "https://api.openai.com/";

        if (StrUtil.isBlank(builder.apiHost)) {
            builder.apiHost = openaiHost;
        }
        apiHost = builder.apiHost;

        if (Objects.isNull(builder.keyStrategy)) {
            List<KeyInfo> collect = builder.apiKey.stream().map(KeyInfo::new).collect(Collectors.toList());
            builder.keyStrategy = new KeyRandomStrategy().initKey(collect);
        }
        keyStrategy = builder.keyStrategy;

        if (Objects.isNull(builder.authInterceptor)) {
            builder.authInterceptor = new DefaultOpenAiAuthInterceptor(keyStrategy);
        }
        authInterceptor = builder.authInterceptor;

        if (Objects.isNull(builder.okHttpClient)) {
            builder.okHttpClient = this.okHttpClient(keyStrategy);
        } else {
            //自定义的okhttpClient  需要增加api keys
            builder.okHttpClient = builder.okHttpClient
                    .newBuilder()
                    .addInterceptor(authInterceptor)
                    .build();
        }
        okHttpClient = builder.okHttpClient;
        OpenAIConst openAIConst = new OpenAIConst();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.openAiApi = new Retrofit.Builder()
                .baseUrl(apiHost)
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .build().create(OpenAiApi.class);
    }


    public static final class Builder {


        /**
         * api keys
         */
        private List<String> apiKey;
        /**
         * api请求地址，结尾处有斜杠
         *
         */
        private String apiHost;
        /**
         * 自定义OkhttpClient
         */
        private OkHttpClient okHttpClient;

        /**
         * api key的获取策略
         */
        private KeyStrategy keyStrategy;

        /**
         * 自定义鉴权拦截器
         */
        private OpenAiAuthInterceptor authInterceptor;

        public Builder() {
        }

        /**
         * @param val api请求地址，结尾处有斜杠
         * @return Builder对象
         */
        public Builder apiHost(String val) {
            apiHost = val;
            return this;
        }

        public Builder apiKey(@NotNull List<String> val) {
            apiKey = val;
            return this;
        }

        public Builder keyStrategy(KeyStrategy val) {
            keyStrategy = val;
            return this;
        }

        public Builder okHttpClient(OkHttpClient val) {
            okHttpClient = val;
            return this;
        }

        public Builder authInterceptor(OpenAiAuthInterceptor val) {
            authInterceptor = val;
            return this;
        }

        public OpenAiStreamClient build() {
            return new OpenAiStreamClient(this);
        }
    }

    /**
     * 流式输出，最新版的GPT-3.5 chat completion 更加贴近官方网站的问答模型
     *
     * @param chatCompletion      问答参数
     * @param eventSourceListener sse监听器
     * @see ConsoleEventSourceListener
     */
    public void streamChatCompletion(ChatCompletion chatCompletion, EventSourceListener eventSourceListener) {
        if (Objects.isNull(eventSourceListener)) {
            log.error("参数异常：EventSourceListener不能为空，可以参考：com.unfbx.chatgpt.sse.ConsoleEventSourceListener");
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        if (!chatCompletion.isStream()) {
            chatCompletion.setStream(true);
        }
        try {
            EventSource.Factory factory = EventSources.createFactory(this.okHttpClient);
            ObjectMapper mapper = new ObjectMapper();
            String requestBody = mapper.writeValueAsString(chatCompletion);
            Request request = new Request.Builder()
                    .url(this.apiHost + "v1/chat/completions")
                    .post(RequestBody.create(MediaType.parse(ContentType.JSON.getValue()), requestBody))
                    .build();
            //创建事件
            EventSource eventSource = factory.newEventSource(request, eventSourceListener);
        } catch (Exception e) {
            log.error("请求参数解析异常", e);
        }
    }

    /**
     * 账户信息查询：里面包含总金额等信息
     *
     * @return 个人账户信息
     */
    public Subscription subscription() {
        Single<Subscription> subscription = this.openAiApi.subscription();
        return subscription.blockingGet();
    }

    /**
     * 账户调用接口消耗金额信息查询 最多查询100天
     *
     * @param starDate 开始时间
     * @param endDate  结束时间
     */
    public BillingUsage billingUsage(@NotNull LocalDate starDate, @NotNull LocalDate endDate) {
        Single<BillingUsage> billingUsage = this.openAiApi.billingUsage(starDate, endDate);
        return billingUsage.blockingGet();
    }


}
