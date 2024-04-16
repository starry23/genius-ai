package cn.apeto.geniusai.server.handler.wechat;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.entity.AccountLog;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.entity.WechatUserInfo;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.RedissonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.mp.builder.outxml.TextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Component
public class LogHandler extends AbstractHandler {

    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private WechatUserInfoService wechatUserInfoService;
    @Autowired
    private InviteLogService inviteLogService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountLogService accountLogService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        this.logger.info("\n接收到请求消息，内容：{}", JSONUtil.toJsonStr(wxMessage));

        // 微信授权登录
        mpLogin(wxMessage, wxMpService);

        UserInfo userInfo = emailUserInfo(wxMessage);
        if (userInfo != null) {
            logger.info("获取到了userInfo {}", userInfo);
            WxMpXmlOutTextMessage wxMpXmlOutTextMessage = mpGive(userInfo.getId(), wxMessage);
            logger.info("返回信息:{}", wxMpXmlOutTextMessage);
            return wxMpXmlOutTextMessage;
        } else {
            return WxMpXmlOutMessage.TEXT().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).content("没有找到您的账号哦~ 请按照标准格式发送 领取代币:您的账号").build();
        }
    }


    private void mpLogin(WxMpXmlMessage wxMessage, WxMpService wxMpService) {

        try {
            String ticket = wxMessage.getTicket();
            String eventKey = wxMessage.getEventKey();
            String event = wxMessage.getEvent();
            logger.info("ticket:{} eventKey:{} event:{}", ticket, eventKey, event);
            if (StrUtil.isBlank(ticket)) {
                return;
            }
            String result = StringRedisUtils.get(RedisKeyEnum.WECHAT_QR_LOGIN_CODE.getKey(ticket));
            logger.info("缓存结果:{}", result);
            if (StrUtil.isBlank(result) || !StrUtil.equals(result, Boolean.FALSE.toString())) {
                return;
            }
            String fromUser = wxMessage.getFromUser();
            WechatUserInfo wechatUserInfo = wechatUserInfoService.getByOpenId(fromUser);
            Long userId;
            if (wechatUserInfo == null) {
                userId = userInfoService.createWechatUserByOpenId(fromUser).getId();

                List<String> split = StrUtil.split(event, "eventKey@");
                if (CollUtil.isNotEmpty(split) && split.size()>1) {
                    inviteLogService.inviteHandler(split.get(1), userId);
                }
            } else {
                userId = wechatUserInfo.getUserId();
            }

//      WxMpUser wxMpUser = wxMpService.getUserService().userInfo(fromUser);

            StpUtil.login(userId);

            StringRedisUtils.setForTimeMIN(RedisKeyEnum.WECHAT_QR_LOGIN_CODE.getKey(ticket), userId.toString(), 10);
        } catch (Exception e) {
            logger.error("微信授权登录异常", e);
        }

    }

    private UserInfo emailUserInfo(WxMpXmlMessage wxMessage) {
        String content = wxMessage.getContent();
        if (StrUtil.containsIgnoreCase(content, "领取")) {
            String[] split = content.split(":");
            String[] split1 = content.split("：");
            String accountNum = "";
            if (split.length > 1) {
                accountNum = split[1];
            } else if (split1.length > 1) {
                accountNum = split1[1];
            }

            if (StrUtil.isBlank(accountNum)) {
                return null;
            }
            UserInfo userInfo = userInfoService.getByEmail(accountNum);
            if (userInfo == null) {
                userInfo = userInfoService.getByPhone(accountNum);
            }
            return userInfo;
        }
        return null;
    }

    private WxMpXmlOutTextMessage mpGive(Long userId, WxMpXmlMessage wxMessage) {
        String giveLockKey = RedisKeyEnum.WECHAT_GIVE_LOCK.getKey(userId);
        TextBuilder textBuilder = WxMpXmlOutMessage.TEXT().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser());

        if (!RedissonUtils.tryLockBoolean(giveLockKey, 5, 10)) {
            logger.warn("分部署锁异常");
            return null;
        }
        try {

            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            Integer mpGiveCurrency = packageWebConfig.getActivitySetting().getMpGiveCurrency();
            if (mpGiveCurrency == null || mpGiveCurrency <= 0) {
                return textBuilder.content("活动以结束~").build();
            }
            Date date = new Date();
            Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.MP;
            List<AccountLog> list = accountLogService.lambdaQuery()
                    .eq(AccountLog::getUserId, userId)
                    .eq(AccountLog::getLogDescriptionType, recharge.getType())
                    .ge(AccountLog::getCreateTime, DateUtil.beginOfDay(date))
                    .le(AccountLog::getCreateTime, DateUtil.endOfDay(date))
                    .list();
            if (CollUtil.isNotEmpty(list)) {
                return textBuilder.content("您今天已经领取过了哦~请明天再来叭。").build();
            }
            // 增加账户余额
            String reqId = IdUtil.fastSimpleUUID();

            Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
            accountService.commonUpdateAccount(userId, BigDecimal.valueOf(mpGiveCurrency), reqId, reqId, recharge, directionTypeEnum, "公众号每日领取");
            return textBuilder.content(StrUtil.format("领取成功 数量:{}", mpGiveCurrency)).build();
        } finally {
            RedissonUtils.unlock(giveLockKey);
        }
    }


}
