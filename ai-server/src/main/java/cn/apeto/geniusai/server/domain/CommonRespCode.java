package cn.apeto.geniusai.server.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author apeto
 * @create 2023/4/3 11:31
 */
@Getter
@AllArgsConstructor
public enum CommonRespCode implements StatusEnumSupport {

  SUCCESS(200, "SUCCESS"),
  ERROR(500, "服务器出了点小问题，请稍后再试试吧~"),
  REQUEST_NOT_SUPPORT(1001, "不支持的请求"),
  REQUEST_NOT_SUPPORT_METHOD(1002, "不支持的请求方式"),
  REQUEST_NOT_SUPPORT_MEDIA_TYPE(1003, "不支持的MediaType"),
  REQUEST_BIND_ARGUMENT_ERROR(1004, "请求绑定参数异常"),
  REQUEST_NOT_READABLE(1005, "请求消息不可读"),
  REQUEST_PARAM_TYPE_MISMATCH(1006, "请求参数类型转换异常"),
  VALID_BEAN_ILLEGAL_ARGUMENT(1007, "属性参数校验失败"),
  VALID_CLASS_ILLEGAL_ARGUMENT(1008, "类参数校验失败"),
  VALID_SERVICE_ILLEGAL_ARGUMENT(1009, "业务层参数校验失败"),
  VALID_REQUEST_PARAM(10015,"请求参数无效"),
  VERIFICATION_CODE(1010, "图形验证码错误"),
  SMS_CODE_ERROR(1011, "短信验证码错误"),
  PASSWORD_ERROR(1012, "账号或者密码错误"),
  SMS_CODE_EXPIRE(1012, "短信验证码失效"),
  IMAGE_CODE_EXPIRE(1013, "图形验证码失效"),
  REQUEST_FREQUENTLY(2001, "亲，手速太快了！"),
  SENSITIVE(2002, "请不要发送敏感信息"),
  VERSION(2003, "版本过低，请升级到最新版本"),

  CARD_IS_NOT_NULL(2004,"生成卡密数量不能为空且数量需大于零"),
  CARD_AMOUNT_IS_NOT_NULL(2005,"生成卡密金额不能为空"),
  CARD_CODE_IS_NOT_NULL(2006,"兑换码不能为空"),
  CARD_CODE_INFO_CANNOT_GET(2007,"获取不到兑换码信息"),
  CARD_CODE_NOT_RIGHT_STATUS(2008,"兑换码不是待兑换状态"),
  CARD_CODE_IS_EXPIRATION(2009,"兑换码已过期"),

  PARTITION_EXSITED(3001,"分区名已存在"),

  SHARE_LINK_VALID(4001,"分享码链接无效")
  ;

  private Integer code;
  private String message;
}
