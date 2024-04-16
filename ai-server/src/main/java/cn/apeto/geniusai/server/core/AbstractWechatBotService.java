package cn.apeto.geniusai.server.core;

import cn.hutool.core.collection.CollUtil;
import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatChoice;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletionResponse;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.embeddings.EmbeddingResponse;
import cn.apeto.geniusai.sdk.entity.embeddings.Item;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.service.BotChatLogService;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanmingyu
 * @create 2024/1/28 8:01 下午
 */
public abstract class AbstractWechatBotService {

    @Autowired
    private BotChatLogService botChatLogService;

    @Autowired
    private OpenAiStreamClient openAiStreamClient;

    public abstract String doChat(ChatInfo chatInfo);

    public String doGptChat(ChatConfigEntity.Entity chatConfig, ChatInfo chatInfo) {
        String wxBotToken = chatInfo.getWxBotToken();
        String wxBotOpenId = chatInfo.getWxBotOpenId();
        Long userId = chatInfo.getUserId();
        String prompt = chatInfo.getPrompt();
        Integer productType = chatInfo.getProductType();


        List<MyMessage> messages = getMessages(wxBotToken, wxBotOpenId, userId, prompt, chatConfig, productType);
        return getAnswer(messages, chatInfo, chatConfig);
    }

    public void questionsAfter(String botToken, String openId, Long userId, String prompt, String answer, Usage usage, ChatEntity chatConfig, Integer productType) {
        botChatLogService.questionsCompleted(botToken, openId, userId, prompt, answer, usage, chatConfig, productType);
    }

    public List<MyMessage> getMessages(String botToken, String openId, Long userId, String prompt, ChatEntity chatConfig, Integer productType) {
        return botChatLogService.getMessages(botToken, openId, userId, prompt, chatConfig, productType);
    }

    public String getAnswer(List<MyMessage> messages, ChatInfo chatInfo, ChatConfigEntity.Entity chatConfig) {

        String model = chatConfig.getModel();
        Integer maxTokens = chatConfig.getMaxTokens();
        String wxBotToken = chatInfo.getWxBotToken();
        String wxBotOpenId = chatInfo.getWxBotOpenId();
        Long userId = chatInfo.getUserId();
        String prompt = chatInfo.getPrompt();
        Integer productType = chatInfo.getProductType();

        List<Message> messageList = messages.stream().map(myMessage -> new Message(myMessage.getRole(), myMessage.getContent(), myMessage.getName())).collect(Collectors.toList());
        if (CollUtil.isNotEmpty(messageList)) {
            int tokens = TikTokensUtil.tokens(model, messageList);
            maxTokens -= tokens;
        }

        ChatCompletion completion = ChatCompletion
                .builder()
                .modelType(chatConfig.getModelType())
                .stream(false)
                .messages(messageList)
                .temperature(chatConfig.getTemperature())
                .maxTokens(maxTokens)
                .frequencyPenalty(chatConfig.getFrequencyPenalty())
                .presencePenalty(chatConfig.getPresencePenalty())
                .model(model).build();

        ChatCompletionResponse chatCompletionResponse = openAiStreamClient.chatCompletion(completion);
        cn.apeto.geniusai.sdk.entity.common.Usage usage1 = chatCompletionResponse.getUsage();
        Usage usage = new Usage();
        usage.setCompletionTokens(usage1.getCompletionTokens());
        usage.setPromptTokens(usage1.getCompletionTokens());
        usage.setTotalTokens(usage1.getTotalTokens());
        ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
        String answer = chatChoice.getMessage().getContent();
        questionsAfter(wxBotToken, wxBotOpenId, userId, prompt, answer, usage, chatConfig, productType);
        return answer;
    }

    public List<MyMessage> getKnowMyMessage(ChatInfo chatInfo, ChatConfigEntity.Entity chatConfig) {

        Integer maxTokens = chatConfig.getMaxTokens();
        String prompt = chatInfo.getPrompt();
        Integer productType = chatInfo.getProductType();
        String wxBotOpenId = chatInfo.getWxBotOpenId();
        String wxBotToken = chatInfo.getWxBotToken();
        Long knowledgeId = chatInfo.getKnowledgeId();
        Long userId = chatInfo.getUserId();
        // 1，根据reqId查询到知识库名称
        String collectionName = MilvusClientUtil.jointCollectionName(knowledgeId, userId);

        // 3，上下文
        List<MyMessage> messages = getMessages(wxBotToken, wxBotOpenId, userId, prompt, chatConfig, productType);

        // 4，向量化上下文
        EmbeddingResponse embed = openAiStreamClient.embeddings(Collections.singletonList(prompt));
        List<List<Float>> qaEmbed = embed.getData().stream().map(Item::getEmbedding).collect(Collectors.toList());

        List<String> loadedSearchContent = MilvusClientUtil.loadSearchContentAsync(qaEmbed, collectionName);

        String sysMessage = MilvusClientUtil.getSystemContent(maxTokens, chatConfig.getModel(), messages, loadedSearchContent, PromptTemplate.LOOSE);

        // 7，以严谨模式设置一个prompt

        messages.add(0, new MyMessage(Message.Role.SYSTEM.getName(), sysMessage));
        return messages;
    }
}
