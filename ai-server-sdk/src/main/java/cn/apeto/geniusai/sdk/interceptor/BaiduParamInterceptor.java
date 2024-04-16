package cn.apeto.geniusai.sdk.interceptor;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.apeto.geniusai.sdk.function.AccessKeyHandler;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
 * @author wanmingyu
 * @create 2023/7/20 8:43 下午
 */

public class BaiduParamInterceptor extends RequestParamInterceptor {


    public BaiduParamInterceptor(AccessKeyHandler accessKeyHandler) {
        super(accessKeyHandler);
    }

    @Override
    String urlParam(HttpUrl url) {
        return url + "?access_token=" + super.accessKeyHandler.getAccessKey();
    }

    @Override
    public @NotNull Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String url = urlParam(original.url());
        RequestBody body = original.body();
        Request request = original.newBuilder()
                .url(url)
                .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                .method(original.method(), body)
                .build();
        return chain.proceed(request);
    }
}
