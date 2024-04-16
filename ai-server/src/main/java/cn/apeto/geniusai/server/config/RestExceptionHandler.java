package cn.apeto.geniusai.server.config;


import cn.apeto.geniusai.server.core.BaseExceptionHandler;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author apeto
 * @create 2023/4/3 11:37
 */
@RestControllerAdvice
public class RestExceptionHandler extends BaseExceptionHandler<ResponseResult<?>> {

  public RestExceptionHandler () {
  }

  protected ResponseResult<?> getResult (Integer code, String message) {
    return ResponseResultGenerator.result(code, message);
  }



}
