package cn.apeto.geniusai.sdk.interceptor;

import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.entity.common.OpenAiResponse;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.sdk.function.KeyStrategy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * 描述：动态处理key的鉴权拦截器
 *
 * @author https:www.unfbx.com
 * @since 2023-04-25
 */
@Getter
@Slf4j
public class DynamicKeyOpenAiAuthInterceptor extends OpenAiAuthInterceptor {

  /**
   * 账号被封了
   */
  private static final String ACCOUNT_DEACTIVATED = "account_deactivated";
  /**
   * key不正确
   */
  private static final String INVALID_API_KEY = "invalid_api_key";
    /**
     *
     */
  private static final String INSUFFICIENT_QUOTA = "insufficient_quota";

  /**
   * 请求头处理
   */
  public DynamicKeyOpenAiAuthInterceptor (KeyStrategy keyStrategy) {
    super(keyStrategy);
  }

  @Override
  public Response intercept (Chain chain) throws IOException {
    Request original = chain.request();
    AuthInfo auth = this.auth(original);
    Request request = auth.getRequest();
    String key = auth.getKey();
    Response response = chain.proceed(request);
    if (!response.isSuccessful()) {
      String errorMsg = response.body().string();
      if (response.code() == CommonError.OPENAI_AUTHENTICATION_ERROR.code()
          || response.code() == CommonError.OPENAI_LIMIT_ERROR.code()
          || response.code() == CommonError.OPENAI_SERVER_ERROR.code()) {
        OpenAiResponse openAiResponse = JSONUtil.toBean(errorMsg, OpenAiResponse.class);
        String errorCode = openAiResponse.getError().getCode();
        log.error("--------> 请求openai异常，错误code：{}", errorCode);
        log.error("--------> 请求异常：{}", errorMsg);
        //账号被封或者key不正确就移除掉
        if (ACCOUNT_DEACTIVATED.equals(errorCode) || INVALID_API_KEY.equals(errorCode) || INSUFFICIENT_QUOTA.equals(errorCode)) {
          onErrorDealApiKeys(key,errorMsg);
        }
        throw new BaseException(openAiResponse.getError().getMessage());
      }
      //非官方定义的错误code
      log.error("--------> 请求异常：{}", errorMsg);
      OpenAiResponse openAiResponse = JSONUtil.toBean(errorMsg, OpenAiResponse.class);
      if (Objects.nonNull(openAiResponse.getError())) {
        log.error(openAiResponse.getError().getMessage());
        throw new BaseException(openAiResponse.getError().getMessage());
      }
      throw new BaseException(CommonError.RETRY_ERROR);
    }
    return response;
  }

  @Override
  public AuthInfo auth (Request original) {
    return super.auth(original);
  }
}
