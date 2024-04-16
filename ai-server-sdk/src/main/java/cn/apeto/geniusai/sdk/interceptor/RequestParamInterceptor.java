package cn.apeto.geniusai.sdk.interceptor;

import cn.apeto.geniusai.sdk.function.AccessKeyHandler;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;

/**
 * @author wanmingyu
 * @create 2023/7/20 8:42 下午
 */
public abstract class RequestParamInterceptor implements Interceptor {

    protected AccessKeyHandler accessKeyHandler;

    public RequestParamInterceptor(AccessKeyHandler accessKeyHandler) {
        this.accessKeyHandler = accessKeyHandler;
    }

    /**
     * URL 拼接参数
     */
    abstract String urlParam(HttpUrl url);

}
