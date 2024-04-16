package cn.apeto.geniusai.sdk.interceptor;

import cn.apeto.geniusai.sdk.function.KeyStrategy;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

/**
 * 描述：请求增加header apikey
 *
 * @author https:www.unfbx.com
 * @since 2023-03-23
 */
@Slf4j
public class DefaultOpenAiAuthInterceptor extends OpenAiAuthInterceptor {

  /**
   * 请求头处理
   */
  public DefaultOpenAiAuthInterceptor (KeyStrategy keyStrategy) {
    super(keyStrategy);
  }

  /**
   * 拦截器鉴权
   *
   * @param chain Chain
   * @return Response对象
   * @throws IOException
   */
  @Override
  public Response intercept (Chain chain) throws IOException {
    Request original = chain.request();
    return chain.proceed(auth(original).getRequest());
  }

}
