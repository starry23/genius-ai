package cn.apeto.geniusai.server.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.ijpay.core.enums.SignType;
import com.ijpay.core.kit.WxPayKit;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.WechatPayConfig;
import cn.apeto.geniusai.server.domain.WxBotAnswer;
import cn.apeto.geniusai.server.entity.BotConfig;
import cn.apeto.geniusai.server.service.BotChatLogService;
import cn.apeto.geniusai.server.service.BotConfigService;
import cn.apeto.geniusai.server.service.PaymentInfoService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.apeto.geniusai.server.utils.wechat.WXBizMsgCrypt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author apeto
 * @create 2023/7/28 18:09
 */
@Slf4j
@RestController
@RequestMapping("/api/callback")
public class CallbackController {

    @Autowired
    private PaymentInfoService paymentInfoService;

    @PostMapping("/wechatPay")
    public String wechatPayCallback(@RequestBody String body) {

        String ok = "<xml>\n" +
                "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                "</xml>";

        log.info("微信付支付回调结果:{}", body);
        JSONObject resJsonObject = XML.toJSONObject(body).getJSONObject("xml");
        Map<String, String> params = new HashMap<>();

        for (Map.Entry<String, Object> stringObjectEntry : resJsonObject) {
            params.put(stringObjectEntry.getKey(), stringObjectEntry.getValue().toString());
        }

        WechatPayConfig wechatPayConfig = CommonUtils.getPaySetting().getWechatPayConfig();

        boolean verifyNotify = WxPayKit.verifyNotify(params, wechatPayConfig.getPartnerKey(), SignType.HMACSHA256);

        if (!verifyNotify) {
            return null;
        }
        String result_code = resJsonObject.getStr("result_code");
        String return_code = resJsonObject.getStr("return_code");
        Constants.PayStateEnum payStateEnum = Constants.PayStateEnum.PAYING;
        if (result_code.equals("SUCCESS") && return_code.equals("SUCCESS")) {
            payStateEnum = Constants.PayStateEnum.SUCCESS;
        }

        String transaction_id = resJsonObject.getStr("transaction_id");
        String paySn = resJsonObject.getStr("out_trade_no");
        String time_end = resJsonObject.getStr("time_end");
        paymentInfoService.callbackHandler(transaction_id, paySn, payStateEnum, DateUtil.parse(time_end, DatePattern.PURE_DATETIME_PATTERN));

        return ok;
    }

    @Autowired
    private BotConfigService botConfigService;
    @Autowired
    private BotChatLogService botChatLogService;


    @PostMapping("/wechatBoot/{appId}")
    public String wechatBoot(@PathVariable("appId") String appId, @RequestBody Map<String, String> encryptedMap) {

        String success = "SUCCESS";

        try {

            BotConfig botConfig = botConfigService.getByAppId(appId);
            if (botConfig == null) {
                log.warn("此appid:{}没有绑定", appId);
                return success;
            }

            String token = botConfig.getToken();
            String aesKey = botConfig.getAesKey();


            WXBizMsgCrypt wxBizMsgCrypt = WXBizMsgCrypt.getWXBizMsgCrypt(token, aesKey, appId);
            String data = wxBizMsgCrypt.decrypt(encryptedMap.get("encrypted"));
            JSONObject jsonObject = XML.toJSONObject(data);
            JSONObject dataJsonObj = jsonObject.getJSONObject("xml");
            // content 字段为复合型数据结构 ,
            // 当 from === 0 时为顾客发送的问题, 可能为纯文本或富文本，
            // 当 from === 1 时为机器人自动回复的内容，
            // 当 from === 2 时为人工客服回复的内容
            Integer from = dataJsonObj.getInt("from");
            //0	智能客服 -- 待接入 (asking)
            //1	人工客服 -- 已接入 (personserving)
            //2	结束人工客服(对话) -- 对话关闭 (complete)
            //3	待人工接入 -- 待转人工 (needperson)
            Integer kfstate = dataJsonObj.getInt("kfstate");
            String openId = dataJsonObj.getStr("userid");
            String createtime = dataJsonObj.getStr("createtime");
            log.info("请求内容为:{}", dataJsonObj.toJSONString(0));

            String wxBotChatReqTargetLockKey = SystemConstants.RedisKeyEnum.WX_BOT_CHAT_REQ_TARGET_LOCK.getKey(token, openId, createtime);


            if (from == 0 && kfstate != null && kfstate == 0) {
                // 当 from === 0 时为顾客发送的问题, 可能为纯文本或富文本
                if (botConfig.getState().equals(1)) {
                    // 上线
                    JSONObject content = dataJsonObj.getJSONObject("content");
                    String prompt = content.getStr("msg");
                    String channel = dataJsonObj.getStr("channel");
                    if (StrUtil.isBlank(prompt)) {
                        return success;
                    }

                    String botChatReqLockKey = SystemConstants.RedisKeyEnum.WX_BOT_CHAT_REQ_LOCK.getKey(token, openId, createtime);
                    if (StringRedisUtils.exists(botChatReqLockKey)) {
                        return success;
                    }
                    // 兼容微信自定义  阻塞
                    String value = StringRedisUtils.leftPop(wxBotChatReqTargetLockKey, 60, TimeUnit.MILLISECONDS);
                    if(StrUtil.isNotBlank(value)){
                        // 说明命中
                        log.info("命中微信内部逻辑 本次不与大模型交互");
                        return success;
                    }

                    WxBotAnswer wxBotAnswer = new WxBotAnswer();
                    wxBotAnswer.setOpenId(openId);
                    wxBotAnswer.setPrompt(prompt);
                    wxBotAnswer.setChannel(channel);
                    wxBotAnswer.setCreateTime(createtime);
                    botChatLogService.wxBotAnswer(botConfig, wxBotAnswer);
                }
            }

            if (from == 1) {
                StringRedisUtils.rightPush(wxBotChatReqTargetLockKey, "1");
                StringRedisUtils.expireSeconds(wxBotChatReqTargetLockKey, 3);

            }
        } catch (Exception e) {
            log.error("加载失败", e);
        }

        return success;
    }
}
