package cn.apeto.geniusai.server.impl;

import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.entity.BotChatLog;
import cn.apeto.geniusai.server.entity.BotConfig;
import cn.apeto.geniusai.server.mapper.BotChatLogMapper;
import cn.apeto.geniusai.server.service.BotChatLogService;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import cn.apeto.geniusai.server.utils.wechat.WXBizMsgCrypt;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * <p>
 * 微信机器人聊天日志 服务实现类
 * </p>
 *
 * @author apeto
 * @since 2024-01-29 10:32:26
 */
@Slf4j
@Service
public class BotChatLogServiceImpl extends ServiceImpl<BotChatLogMapper, BotChatLog> implements BotChatLogService {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void questionsCompleted(String botToken, String openId, Long userId, String prompt, String answer, Usage usage, ChatEntity chatConfig, Integer productType) {
        // 处理问题完成后的逻辑
        BotChatLog userChatLog = new BotChatLog();
        userChatLog.setUserId(userId);
        userChatLog.setChatRole("user");
        userChatLog.setContent(prompt);
        userChatLog.setOpenId(openId);
        save(userChatLog);

        BotChatLog answerChatLog = new BotChatLog();
        answerChatLog.setUserId(userId);
        answerChatLog.setChatRole("assistant");
        answerChatLog.setContent(answer);
        answerChatLog.setOpenId(openId);
        save(answerChatLog);

        // 提问放入缓存
        String redisKey = SystemConstants.RedisKeyEnum.WX_BOT_CHAT_LOG.getKey(botToken, openId);
        Integer contextSplit = chatConfig.getContextSplit();
        for (String splitStr : StrUtil.split(prompt, contextSplit)) {
            StringRedisUtils.add(redisKey, JSONUtil.toJsonStr(new Message(Message.Role.USER.getName(), splitStr, userId.toString())), answerChatLog.getId());
        }

        // 消费
        exchangeCardDetailService.consume(userId, productType, openId, usage.getTotalTokens());

        // 设置过期时间 1小时
        StringRedisUtils.expireSeconds(redisKey, Constants.RedisExpireTime.ONE_DAY.getValue());
    }

    @Override
    public List<MyMessage> getMessages(String botToken, String openId, Long userId, String prompt, ChatEntity chatConfig, Integer productType) {

        // key
        String redisKey = SystemConstants.RedisKeyEnum.WX_BOT_CHAT_LOG.getKey(botToken, openId);

        // 上下文限制
        int limit = chatConfig.getContextLimit();
        String model = chatConfig.getModel();
        Integer contextSplit = chatConfig.getContextSplit();
        int totalLimit = limit * contextSplit;
        Constants.ProductTypeEnum productTypeEnum = Constants.ProductTypeEnum.getByEnum(productType);
        Constants.ServiceEnum serviceEnum = productTypeEnum.getServiceEnum();
        List<MyMessage> messages = new ArrayList<>();

        // 大文本不返回
        Set<String> strings = StringRedisUtils.reverseRangeByScore(redisKey, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 0, limit);

        int current = 0;
        boolean checkMessage = serviceEnum.equals(Constants.ServiceEnum.GPT) || serviceEnum.equals(Constants.ServiceEnum.SPARK_DESK);
        if (CollectionUtil.isNotEmpty(strings)) {
            for (String str : strings) {
                if (checkMessage) {
                    current += TikTokensUtil.tokens(model, str);
                    if (current + current > totalLimit) {
                        break;
                    }
                }
                messages.add(0, JSONUtil.toBean(str, MyMessage.class));
            }
        } else {
            List<BotChatLog> botChatLogs = selectTokenAndOpenId(botToken, openId);
            for (BotChatLog botChatLog : botChatLogs) {
                String content = botChatLog.getContent();
                String chatRole = botChatLog.getChatRole();
                if (checkMessage) {
                    // gpt
                    for (String splitContent : StrUtil.split(content, contextSplit)) {
                        current += TikTokensUtil.tokens(model, content);
                        if (current + current > totalLimit) {
                            break;
                        }
                        MyMessage message = new MyMessage(chatRole, splitContent, userId.toString());
                        if (messages.size() >= limit) {
                            break;
                        }
                        messages.add(0, message);
                        StringRedisUtils.add(redisKey, JSONUtil.toJsonStr(message), botChatLog.getId());

                    }
                }
            }
        }
        messages.add(new MyMessage(Message.Role.USER.getName(), prompt));
        return messages;
    }

    @Override
    @Async("chat")
    public void wxBotAnswer(BotConfig botConfig, WxBotAnswer wxBotAnswer) {

        if (botConfig == null) {
            return;
        }

        String createTime = wxBotAnswer.getCreateTime();
        String channel = wxBotAnswer.getChannel();
        String openId = wxBotAnswer.getOpenId();
        String prompt = wxBotAnswer.getPrompt();

        String appId = botConfig.getAppId();
        String token = botConfig.getToken();
        String aesKey = botConfig.getAesKey();
        Integer productType = botConfig.getProductType();
        Long userId = botConfig.getUserId();
        String kefuName = botConfig.getKefuName();
        String kefuAvatar = botConfig.getKefuAvatar();
        Long knowledgeId = botConfig.getKnowledgeId();

        String botChatReqLockKey = SystemConstants.RedisKeyEnum.WX_BOT_CHAT_REQ_LOCK.getKey(token, openId, createTime);

        try {
            // 5分失效 防止微信bot多次回调
            StringRedisUtils.setForTimeMIN(botChatReqLockKey, "1", 5);
            WXBizMsgCrypt wxBizMsgCrypt = WXBizMsgCrypt.getWXBizMsgCrypt(token, aesKey, appId);

            Constants.WxBotProductTypeEnum wxBotProductTypeEnum = Constants.WxBotProductTypeEnum.getByType(productType);
            if (wxBotProductTypeEnum != null) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setPrompt(prompt);
                chatInfo.setProductType(productType);
                chatInfo.setUserId(userId);
                chatInfo.setWxBotOpenId(openId);
                chatInfo.setWxBotToken(token);
                chatInfo.setKnowledgeId(knowledgeId);
                String answer = SpringUtil.getBean(wxBotProductTypeEnum.getChatClass()).doChat(chatInfo);
                String url = "https://chatbot.weixin.qq.com/openapi/sendmsg/" + token;
                Map<String, String> params = new HashMap<>();
                params.put("appid", token);
                params.put("openid", openId);
                params.put("msg", answer);
                params.put("channel", channel);
                params.put("kefuname", kefuName);
                params.put("kefuavatar", OSSFactory.build().getFullUrl(kefuAvatar));
                String xmlStr = "<xml>" + JSONUtil.toXmlStr(JSONUtil.parse(params)) + "</xml>";
                String encrypted = wxBizMsgCrypt.encrypt(wxBizMsgCrypt.getRandomStr(), xmlStr);
                String encrypt = JSONUtil.toJsonStr(MapUtil.builder().put("encrypt", encrypted).build());
                String res = HttpUtil.post(url, encrypt);
                log.info("微信机器人响应结果:{}", res);
            }
        } catch (Exception e) {
            log.error("微信机器人回复异常", e);

        } finally {
            StringRedisUtils.delete(botChatReqLockKey);
        }
    }

    @Override
    public List<BotChatLog> selectTokenAndOpenId(String botToken, String openId) {

        LambdaQueryWrapper<BotChatLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BotChatLog::getBotToken, botToken);
        queryWrapper.eq(BotChatLog::getOpenId, openId);
        return baseMapper.selectList(queryWrapper);
    }
}
