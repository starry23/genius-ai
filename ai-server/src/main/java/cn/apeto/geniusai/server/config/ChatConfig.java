package cn.apeto.geniusai.server.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.apeto.geniusai.sdk.BaiduAiStreamClient;
import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.OpenAiStreamClient.Builder;
import cn.apeto.geniusai.sdk.interceptor.BaiduParamInterceptor;
import cn.apeto.geniusai.sdk.interceptor.DynamicKeyOpenAiAuthInterceptor;
import cn.apeto.geniusai.sdk.interceptor.OpenAILogger;
import cn.apeto.geniusai.server.config.properties.ChatConfigProperties;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.concurrent.TimeUnit;

/**
 * @author apeto
 * @create 2023/3/31 9:11 下午
 */
@Configuration
public class ChatConfig {

    @Autowired
    private ChatConfigProperties chatConfigProperties;
    @Autowired
    private MyKeyStrategy myKeyStrategy;

    @Bean
    public OpenAiStreamClient openAiStreamClient() {
        String apiHost = chatConfigProperties.getApiHost();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder()
                .addInterceptor(httpLoggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(600, TimeUnit.SECONDS)
                .readTimeout(600, TimeUnit.SECONDS);

        Builder builder = OpenAiStreamClient.builder().apiHost(apiHost).keyStrategy(myKeyStrategy).authInterceptor(new DynamicKeyOpenAiAuthInterceptor(myKeyStrategy));
        if (SpringUtil.getProperty("spring.profiles.active").equals("local")) {
            // 代理
            Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("127.0.0.1", 7890));
            okHttpClientBuilder.proxy(proxy);
        }
        builder.okHttpClient(okHttpClientBuilder.build());
        return builder.build();
    }

    @Autowired
    private BaiduAccessToken baiduAccessToken;

    @Bean
    public BaiduAiStreamClient baiduAiStreamClient() {

        return BaiduAiStreamClient.builder().paramInterceptor(new BaiduParamInterceptor(baiduAccessToken)).build();
    }


}
