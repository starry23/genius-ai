package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Item;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.PromptTemplate;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.entity.ShareItem;
import cn.apeto.geniusai.server.listener.BaseWebSocketSourceListener;
import cn.apeto.geniusai.server.listener.OpenAIWebSocketEventSourceListener;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import cn.apeto.geniusai.server.service.ShareItemDetailLogService;
import cn.apeto.geniusai.server.service.ShareItemService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/06/13:09
 * @Description:
 */
@Slf4j
@Service
public class KnowledgeShareServiceImpl extends AbstractWebSocketChatService{

    @Autowired
    private OpenAiStreamClient openAiStreamClient;

    @Autowired
    private ShareItemService shareItemService;

    @Autowired
    private ShareItemDetailLogService shareItemDetailLogService;

    @Autowired
    private KnowledgeChatBindingService knowledgeChatBindingService;


    @Override
    public void doChat(WebSocketSession session, String reqId, Long userId, String prompt, Boolean internet) {


        KnowledgeChatBinding knowledgeChatBinding = knowledgeChatBindingService.getByReqId(reqId);
        ShareItem shareItem = shareItemService.getByUidAndItemId(userId,knowledgeChatBinding.getKnowledgeId());

        String collectionName = MilvusClientUtil.jointCollectionName(shareItem.getItemId(),userId);

        BaseWebSocketSourceListener eventSourceListener = new OpenAIWebSocketEventSourceListener(session);

        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig = chatConfigEntity.getEntity3();

        // 6 上下文
        MyMessage myMessage = new MyMessage(Message.Role.USER.getName(), prompt);
        List<MyMessage> messages = new ArrayList<>();
        messages.add(myMessage);

        // 7 向量搜索
        EmbeddingResponse embed = openAiStreamClient.embeddings(Collections.singletonList(prompt));


        List<List<Float>> qaEmbed = embed.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());
        List<String> orderedCandidates = MilvusClientUtil.loadSearchContentAsync(qaEmbed, collectionName);
        String sysMessage = MilvusClientUtil.getSystemContent(chatConfig.getMaxTokens(), chatConfig.getModel(), messages, orderedCandidates,PromptTemplate.RIGOROUS);

        messages.add(0, new MyMessage(Message.Role.SYSTEM.getName(), sysMessage));

        ChatCompletion completion = CommonUtils.packageCompletion(messages, chatConfig);

        eventSourceListener.setOnComplete((answer, usage) -> {
            usage.setCompletionTokens((long) TikTokensUtil.tokens(chatConfig.getModel(), answer));
            usage.setPromptTokens((long) TikTokensUtil.tokens(chatConfig.getModel(), prompt));
            usage.setTotalTokens(completion.tokens() + usage.getCompletionTokens());
            shareItemDetailLogService.questionsCompleted(shareItem.getId(), reqId, userId, prompt, answer, usage, Constants.ProductTypeEnum.CHAT_KNOWLEDGE.getType());
        });

        openAiStreamClient.streamChatCompletion(completion, eventSourceListener);
    }
}
