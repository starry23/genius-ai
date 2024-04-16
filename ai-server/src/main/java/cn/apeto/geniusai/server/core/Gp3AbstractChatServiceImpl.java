package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.utils.CommonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author apeto
 * @create 2023/7/20 10:54 下午
 */
@Component
public class Gp3AbstractChatServiceImpl extends AbstractChatService {


    @Override
    public void doChat(SseEmitter sseEmitter, ChatInfo chatInfo) {
        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig = chatConfigEntity.getEntity3();
        doGptChat(sseEmitter, chatConfig,chatInfo);
    }
}
