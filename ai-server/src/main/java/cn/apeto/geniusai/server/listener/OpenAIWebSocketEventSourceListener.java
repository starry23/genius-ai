package cn.apeto.geniusai.server.listener;

import cn.apeto.geniusai.sdk.entity.chat.ChatChoice;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletionResponse;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.exception.CommonError;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/15:37
 * @Description:
 */
@Slf4j
public class OpenAIWebSocketEventSourceListener extends BaseWebSocketSourceListener {


    public OpenAIWebSocketEventSourceListener(WebSocketSession session) {
        super(session);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立webSocket连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {

        if (data.equals(DONE_SIGNAL)) {
            return;
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class); // 读取Json

        List<ChatChoice> choices = completionResponse.getChoices();
        ChatChoice chatChoice = choices.get(0);
        Message delta = chatChoice.getDelta();
        if (CollectionUtil.isEmpty(choices) || delta == null) {
            log.warn("CollectionUtil.isEmpty(choices) || delta == null 这个是空");
            return;
        }
        if ("stop".equals(chatChoice.getFinishReason())) {
            return;
        }
        String content = delta.getContent();
        if (log.isDebugEnabled()) {
            log.debug("content信息为:{}", content);
        }
        if (StrUtil.isBlank(content)) {
            if (log.isDebugEnabled()) {
                log.debug("信息为空:{}", content);
            }
            return;
        }
        if ("".equals(content)) {
            delta.setContent("\n");
            content = "\n";
        }

        delta.setRole(Message.Role.ASSISTANT.getName());
        lastMessage += content;

        session.sendMessage(new TextMessage(JSONUtil.toJsonStr(delta)));

    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("OpenAI关闭sse连接...");
        try {
            session.sendMessage(new TextMessage("DONE"));
            onComplete.accept(lastMessage, usage);
        }catch (Exception e){
            log.error("关闭连接异常",e);
        }

    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {

        try {
            String res = JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.ERROR));

            if (t != null) {
                if (t instanceof IOException) {
                    log.error("网络错误！{}", t.getMessage());
                } else {
                    log.error("服务错误！{}", t.getMessage(), t);
                }
            }

            if (Objects.isNull(response)) {
                log.error("OpenAI  sse连接异常", t);
            }

            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                int code = response.code();
                if (code == 200) {
//                    return;
                }

                CommonError commonError = CommonError.getCommonError(code);
                if (commonError != null) {
                    log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), commonError.msg());
                    res = JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.ERROR.getCode(), commonError.msg()));
                }

            }

            session.sendMessage(new TextMessage(res));
        } finally {
            if (StrUtil.isNotBlank(lastMessage)) {
                onComplete.accept(lastMessage, usage);
            }
            if (response != null) {
                response.close();
            }
            if (eventSource != null) {
                eventSource.cancel();
            }

        }
    }
}

