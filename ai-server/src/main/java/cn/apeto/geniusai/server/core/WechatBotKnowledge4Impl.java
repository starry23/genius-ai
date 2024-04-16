package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.utils.CommonUtils;
import org.springframework.stereotype.Component;

/**
 * @author wanmingyu
 * @create 2024/1/28 8:01 下午
 */
@Component
public class WechatBotKnowledge4Impl extends AbstractWechatBotService {


    @Override
    public String doChat(ChatInfo chatInfo) {
        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig = chatConfigEntity.getEntity4();
        return getAnswer(getKnowMyMessage(chatInfo, chatConfig), chatInfo, chatConfig);
    }
}
