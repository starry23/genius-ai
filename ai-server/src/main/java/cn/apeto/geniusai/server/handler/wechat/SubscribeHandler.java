package cn.apeto.geniusai.server.handler.wechat;

import cn.apeto.geniusai.server.builder.TextBuilder;
import cn.apeto.geniusai.server.service.UserInfoService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@Component
public class SubscribeHandler extends AbstractHandler {

  @Autowired
  private UserInfoService userInfoService;

  @Override
  public WxMpXmlOutMessage handle (WxMpXmlMessage wxMessage,
      Map<String, Object> context, WxMpService weixinService,
      WxSessionManager sessionManager) throws WxErrorException {

    String eventKey = wxMessage.getEventKey();
    // 获取微信用户基本信息
//    try {
    WxMpUser userWxInfo = new WxMpUser();
    userWxInfo.setOpenId(wxMessage.getFromUser());
//      if (userWxInfo != null) {
//    Long userId = userInfoService.getOrCreateWechatUser(userWxInfo);
//    StringRedisUtils.setForTimeMIN(RedisKeyEnum.WECHAT_QR_LOGIN_CODE.getKey(eventKey), userId.toString(), 10);

//      }
//    } catch (WxErrorException e) {
//      if (e.getError().getErrorCode() == 48001) {
//        log.info("该公众号没有获取用户信息权限！");
//      }
//    }

    WxMpXmlOutMessage responseResult = null;
    try {
      responseResult = handleSpecial(wxMessage);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    if (responseResult != null) {
      return responseResult;
    }

    try {
      return new TextBuilder().build("感谢关注 回复”领取:您的账号“，即可获得免费次数 快来领取吧~ 举个例子:领取:GeniusAi@gmail.com", wxMessage, weixinService);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return null;
  }

  /**
   * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
   */
  private WxMpXmlOutMessage handleSpecial (WxMpXmlMessage wxMessage)
      throws Exception {
    //TODO
    return null;
  }

}
