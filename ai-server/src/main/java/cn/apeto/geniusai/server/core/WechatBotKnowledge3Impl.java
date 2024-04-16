package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Item;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.PromptTemplate;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanmingyu
 * @create 2024/1/28 8:01 下午
 */
@Component
public class WechatBotKnowledge3Impl extends AbstractWechatBotService {


    @Override
    public String doChat(ChatInfo chatInfo) {

        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig = chatConfigEntity.getEntity3();
        return getAnswer(getKnowMyMessage(chatInfo, chatConfig), chatInfo, chatConfig);
    }


}
