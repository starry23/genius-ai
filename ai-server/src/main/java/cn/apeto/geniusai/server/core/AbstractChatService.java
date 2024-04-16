package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatChoice;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletionResponse;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.entity.FileInfo;
import cn.apeto.geniusai.server.entity.SessionRecord;
import cn.apeto.geniusai.server.listener.OpenAISSEEventSourceListener;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.service.FileInfoService;
import cn.apeto.geniusai.server.service.SessionRecordService;
import cn.apeto.geniusai.server.service.oss.CloudStorageService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.SerpapiUtils;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/7/20 10:53 下午
 */
@Slf4j
public abstract class AbstractChatService {

    @Autowired
    private ChatDetailLogService chatDetailLogService;
    @Autowired
    private OpenAiStreamClient openAiStreamClient;
    @Autowired
    private FileInfoService fileInfoService;
    @Autowired
    private SessionRecordService sessionRecordService;

    private Boolean isStream = true;

    public AbstractChatService(Boolean isStream) {
        this.isStream = isStream;
    }

    public AbstractChatService() {
    }

    public abstract void doChat(SseEmitter sseEmitter, ChatInfo chatInfo);

    protected void doGptChat(SseEmitter sseEmitter, ChatConfigEntity.Entity chatConfig, ChatInfo chatInfo) {

        Long userId = chatInfo.getUserId();
        Integer productType = chatInfo.getProductType();
        String prompt = chatInfo.getPrompt();
        String reqId = chatInfo.getReqId();
        Boolean internet = chatInfo.getInternet();
        String logType = chatInfo.getLogType();
        String model = chatInfo.getModel();

        model = StrUtil.isBlank(model) ? chatConfig.getModel() : model;

        OpenAISSEEventSourceListener openAIEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter);
        Integer maxToken = chatConfig.getMaxTokens();
        Integer contextLimit = chatConfig.getContextLimit();
        Integer contextSplit = chatConfig.getContextSplit();

        // 附件填充&获取messages
        List<MyMessage> messages = buildFile(getMessages(userId, reqId, prompt, chatConfig, productType), userId, reqId);

        if (internet) {
            MyMessage messageSearchBaiduSnippets = SerpapiUtils.createMessageSearchBaiduSnippets(prompt, model, maxToken - contextLimit * contextSplit);
            if (messageSearchBaiduSnippets != null) {
                // 联网
                messages.remove(0);

                messages.add(0, messageSearchBaiduSnippets);
            }
        }
        List<Message> messageList = messages.stream()
                .map(myMessage -> new Message(myMessage.getRole(), myMessage.getContent(), myMessage.getName()))
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(messageList)) {
            int tokens = TikTokensUtil.tokens(model, messageList);
            maxToken -= tokens;
        }
        ChatCompletion.ChatCompletionBuilder<?, ?> chatCompletionBuilder = ChatCompletion
                .builder()
                .modelType(chatConfig.getModelType())
                .messages(messageList)
                .temperature(chatConfig.getTemperature())
                .maxTokens(maxToken)
                .frequencyPenalty(chatConfig.getFrequencyPenalty())
                .presencePenalty(chatConfig.getPresencePenalty())
                .model(model);
        if (isStream) {
            // 流
            chatCompletionBuilder.stream(true);
            ChatCompletion completion = chatCompletionBuilder.build();
            String finalModel = model;
            openAIEventSourceListener.setOnComplete((msg, usage) -> {
                usage.setCompletionTokens((long) TikTokensUtil.tokens(finalModel, msg));
                usage.setPromptTokens((long) TikTokensUtil.tokens(finalModel, prompt));
                usage.setTotalTokens(completion.tokens() + usage.getCompletionTokens());
                questionsAfter(reqId, userId, prompt, msg, usage, chatConfig, productType, logType);
            });
            openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
        } else {
            chatCompletionBuilder.stream(false);
            ChatCompletionResponse chatCompletionResponse = openAiStreamClient.chatCompletion(chatCompletionBuilder.build());
            cn.apeto.geniusai.sdk.entity.common.Usage usage1 = chatCompletionResponse.getUsage();
            Usage usage = new Usage();
            usage.setCompletionTokens(usage1.getCompletionTokens());
            usage.setPromptTokens(usage1.getCompletionTokens());
            usage.setTotalTokens(usage1.getTotalTokens());
            ChatChoice chatChoice = chatCompletionResponse.getChoices().get(0);
            questionsAfter(reqId, userId, prompt, chatChoice.getMessage().getContent(), usage, chatConfig, productType, logType);
        }


    }

    public void questionsAfter(String reqId, Long userId, String prompt, String msg, Usage usage, ChatEntity chatConfig, Integer productType, String logType) {
        chatDetailLogService.questionsCompleted(reqId, userId, prompt, msg, usage, chatConfig, productType, logType);
    }


    public List<MyMessage> getMessages(Long userId, String reqId, String prompt, ChatEntity chatConfig, Integer productType) {
        return chatDetailLogService.getMessage(userId, reqId, prompt, chatConfig, productType);
    }


    private List<MyMessage> buildFile(List<MyMessage> messages, Long userId, String reqId) {
        SessionRecord sessionRecord = sessionRecordService.getUidAndReqId(userId, reqId);
        Long fileId = sessionRecord.getFileId();
        if (fileId != null && fileId > 0) {
            StringBuilder content = new StringBuilder();
            MyMessage myMessage = messages.get(0);
            String systemName = Message.Role.SYSTEM.getName();

            String searchStr = "my files:";

            if (myMessage != null && systemName.equals(myMessage.getRole())) {
                // 说明已存在system 需要追加
                String messageContent = myMessage.getContent();
                if (messageContent.contains(searchStr)) {
                    int indexOf = StrUtil.indexOf(messageContent, searchStr, 0, false);
                    String subSuf = StrUtil.subPre(messageContent, indexOf + searchStr.length());
                    content.append(subSuf);
                } else {
                    content.append(messageContent).append(searchStr);
                }

            } else {
                content.append(searchStr);
            }

            // 遍历多个附件
            FileInfo fileInfo = fileInfoService.getById(fileId);
            if (fileInfo == null) {
                return messages;
            }

            Integer type = fileInfo.getType();
            String path = fileInfo.getPath();
            CloudStorageService cloudStorageService = OSSFactory.getServicesByType(type);
            String fullUrl = cloudStorageService.getFullUrl(path);
            content.append(fullUrl).append(" ").append(StrUtil.COMMA);
            messages.add(0, new MyMessage(systemName, content.substring(0, content.length() - 1)));

        }
        return messages;
    }

}
