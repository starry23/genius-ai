package cn.apeto.geniusai.server.core;

import cn.apeto.geniusai.sdk.SparkDeskClient;
import cn.apeto.geniusai.sdk.entity.sparkEntiy.*;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.listener.SparkDeskSSEChatListener;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/7/20 10:54 下午
 */
@Component
public class SparkDeskChatServiceImpl extends AbstractChatService {

    @Autowired
    private ChatDetailLogService chatDetailLogService;
    @Resource(name = "sparkDeskOkHttpClient")
    private OkHttpClient sparkDeskOkHttpClient;

    @Override
    public void doChat(SseEmitter sseEmitter, ChatInfo chatInfo) {

        Integer productType = chatInfo.getProductType();
        String prompt = chatInfo.getPrompt();
        String reqId = chatInfo.getReqId();
        Long userId = chatInfo.getUserId();
        String logType = chatInfo.getLogType();

        ChatConfigEntity chatConfigEntity = CommonUtils.getChatConfig();
        ChatConfigEntity.SparkDeskEntity sparkDesk = chatConfigEntity.getSparkDesk();

        SparkDeskClient sparkDeskClient = SparkDeskClient.builder()
                .host(sparkDesk.getHost())
                .appid(sparkDesk.getAppid())
                .apiKey(sparkDesk.getApiKey())
                .apiSecret(sparkDesk.getApiSecret())
                .okHttpClient(sparkDeskOkHttpClient)
                .build();
        List<MyMessage> messages = getMessages(userId, reqId, prompt, sparkDesk, productType);
        List<Text> textList = messages.stream().map(myMessage -> {
            Text text = new Text();
            text.setContent(myMessage.getContent());
            text.setRole(myMessage.getRole());
            return text;
        }).collect(Collectors.toList());
        //构建请求参数
        InHeader header = InHeader.builder().uid(UUID.randomUUID().toString().substring(0, 10)).appid(sparkDesk.getAppid()).build();
        Parameter parameter = Parameter.builder().chat(Chat.builder().domain(sparkDesk.getDomain()).maxTokens(sparkDesk.getMaxTokens())
                .temperature(sparkDesk.getTemperature()).topK(sparkDesk.getTopK()).build()).build();
        InPayload payload = InPayload.builder().message(Message.builder().text(textList).build()).build();
        AIChatRequest aiChatRequest = AIChatRequest.builder().header(header).parameter(parameter).payload(payload).build();
        SparkDeskSSEChatListener chatListener = new SparkDeskSSEChatListener(aiChatRequest, sseEmitter);
        sparkDeskClient.chat(chatListener);
        chatListener.getBaseEventSourceListener().setOnComplete((msg, usage) -> chatDetailLogService.questionsCompleted(reqId, userId, prompt, msg, usage, sparkDesk, productType, logType));
    }
}
