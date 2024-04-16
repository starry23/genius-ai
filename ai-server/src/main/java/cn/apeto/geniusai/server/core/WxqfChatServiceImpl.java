package cn.apeto.geniusai.server.core;

import cn.hutool.core.collection.CollUtil;
import cn.apeto.geniusai.sdk.BaiduAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletion;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.listener.BaiduAISSEEventSourceListener;
import cn.apeto.geniusai.server.service.ChatDetailLogService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/7/20 10:54 下午
 */
@Slf4j
@Component
public class WxqfChatServiceImpl extends AbstractChatService {

    @Autowired
    private BaiduAiStreamClient baiduAiStreamClient;
    @Autowired
    private ChatDetailLogService chatDetailLogService;

    @Override
    public void doChat(SseEmitter sseEmitter, ChatInfo chatInfo) {

        String reqId = chatInfo.getReqId();
        String prompt = chatInfo.getPrompt();
        Long userId = chatInfo.getUserId();
        Integer productType = chatInfo.getProductType();
        String logType = chatInfo.getLogType();

        ChatConfigEntity chatConfig = CommonUtils.getChatConfig();
        ChatConfigEntity.WXQFEntity wxqfEntity = chatConfig.getWxqfEntity();
        BaiduAISSEEventSourceListener openAIEventSourceListener = new BaiduAISSEEventSourceListener(sseEmitter);
        openAIEventSourceListener.setOnComplete((msg, usage) -> chatDetailLogService.questionsCompleted(reqId, userId, prompt, msg, usage, wxqfEntity, productType,logType));
        BaiduCompletion baiduCompletion = new BaiduCompletion();
        List<MyMessage> messages = getMessages(userId, reqId, prompt, wxqfEntity, productType);
        reformatMessages(messages);
        if (CollUtil.isEmpty(messages)) {
            messages = new ArrayList<>();
            messages.add(new MyMessage(Message.Role.USER.getName(), prompt));
        }
        List<BaiduCompletion.Message> messageList = messages.stream().map(myMessage -> {
            BaiduCompletion.Message message = new BaiduCompletion.Message();
            message.setRole(myMessage.getRole());
            message.setContent(myMessage.getContent());
            return message;
        }).collect(Collectors.toList());
        baiduCompletion.setMessages(messageList);
        baiduCompletion.setStream(true);
        baiduCompletion.setUser_id(userId.toString());
        baiduCompletion.setTemperature(wxqfEntity.getTemperature());
        baiduCompletion.setTop_p(wxqfEntity.getTop_p());
        baiduCompletion.setPenalty_score(wxqfEntity.getPenalty_score());
        baiduAiStreamClient.streamChatCompletion(baiduCompletion, openAIEventSourceListener);
    }

    public static void reformatMessages(List<MyMessage> messages) {
        Iterator<MyMessage> iterator = messages.iterator();
        String prevRole = "";


        while (iterator.hasNext()) {
            MyMessage msg = iterator.next();
            String role = msg.getRole();
            if (role.equals("assistant") || role.equals(prevRole)) {
                iterator.remove();
            } else {
                prevRole = role;
            }
        }

        // If list size is even or the only message is not 'user', remove the last message
        if (!messages.isEmpty() && (messages.size() % 2 == 0 || (!messages.get(messages.size() - 1).getRole().equals("user")))) {
            messages.remove(messages.size() - 1);
        }

        if(messages.size()==1 && messages.get(0).getRole().equals("user")){
            messages.clear();
        }
    }

}
