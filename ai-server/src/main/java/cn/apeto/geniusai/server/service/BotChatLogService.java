package cn.apeto.geniusai.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.apeto.geniusai.server.domain.ChatEntity;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.Usage;
import cn.apeto.geniusai.server.domain.WxBotAnswer;
import cn.apeto.geniusai.server.entity.BotChatLog;
import cn.apeto.geniusai.server.entity.BotConfig;

import java.util.List;

/**
 * <p>
 * 微信机器人聊天日志 服务类
 * </p>
 *
 * @author apeto
 * @since 2024-01-29 10:32:26
 */
public interface BotChatLogService extends IService<BotChatLog> {

    List<BotChatLog> selectTokenAndOpenId(String botToken, String openId);


    void questionsCompleted(String botToken, String openId, Long userId, String prompt, String answer, Usage usage, ChatEntity chatConfig, Integer productType);

    List<MyMessage> getMessages(String botToken, String openId, Long userId, String prompt, ChatEntity chatConfig, Integer productType);

    void wxBotAnswer(BotConfig botConfig, WxBotAnswer wxBotAnswer);
}
