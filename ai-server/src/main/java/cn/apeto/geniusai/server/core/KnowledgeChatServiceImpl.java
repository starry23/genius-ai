package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Item;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.listener.KnowledgeOpenAISSEEventSourceListener;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/8/1 12:54
 */
@Slf4j
@Component
public class KnowledgeChatServiceImpl extends AbstractChatService {

    @Autowired
    private KnowledgeChatBindingService knowledgeChatBindingService;
    @Autowired
    private OpenAiStreamClient openAiStreamClient;
    @Autowired
    private ChatDetailLogService chatDetailLogService;


    @Override
    public void doChat(SseEmitter sseEmitter, ChatInfo chatInfo) {

        String prompt = chatInfo.getPrompt();
        String reqId = chatInfo.getReqId();
        Long userId = chatInfo.getUserId();
        String logType = chatInfo.getLogType();
        // 1，根据reqId查询到知识库名称
        KnowledgeChatBinding knowledgeChatBinding = knowledgeChatBindingService.getByReqId(reqId);
        Long knowledgeId = knowledgeChatBinding.getKnowledgeId();
        String collectionName = MilvusClientUtil.jointCollectionName(knowledgeId,userId);

        KnowledgeOpenAISSEEventSourceListener openAIEventSourceListener = new KnowledgeOpenAISSEEventSourceListener(sseEmitter);

        // 2，maxtoken
        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig = chatConfigEntity.getEntity3();
        Integer maxTokens = chatConfig.getMaxTokens();

        // 3，上下文
        List<MyMessage> messages = getMessages(userId, reqId, prompt, chatConfig, Constants.ProductTypeEnum.GPT3_5.getType());

        // 4，向量化上下文
        EmbeddingResponse embed = openAiStreamClient.embeddings(Collections.singletonList(prompt));
        List<List<Float>> qaEmbed = embed.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());

        List<String> loadedSearchContent = MilvusClientUtil.loadSearchContentAsync(qaEmbed, collectionName);

        String sysMessage = MilvusClientUtil.getSystemContent(maxTokens, chatConfig.getModel(), messages, loadedSearchContent,PromptTemplate.LOOSE);

        // 7，以严谨模式设置一个prompt

        messages.add(0, new MyMessage(Message.Role.SYSTEM.getName(), sysMessage));

        ChatCompletion completion = CommonUtils.packageCompletion(messages, chatConfig);

        // 9 设置回调函数 进行数据库缓存记录问答，和代币消耗
        openAIEventSourceListener.setOnComplete((msg, usage) -> {
            usage.setCompletionTokens((long) TikTokensUtil.tokens(chatConfig.getModel(), msg));
            usage.setPromptTokens((long) TikTokensUtil.tokens(chatConfig.getModel(), prompt));
            usage.setTotalTokens(completion.tokens() + usage.getCompletionTokens());
            chatDetailLogService.questionsCompleted(reqId, userId, prompt, msg, usage, chatConfig, Constants.ProductTypeEnum.CHAT_KNOWLEDGE.getType(),logType);
        });

        openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
    }
}
