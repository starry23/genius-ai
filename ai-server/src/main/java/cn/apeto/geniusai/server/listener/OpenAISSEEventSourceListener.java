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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Objects;

/**
 * 描述：OpenAIEventSourceListener
 *
 * @author warape
 * @date 2023-02-22
 */
@Slf4j
public class OpenAISSEEventSourceListener extends BaseEventSourceListener {

    private String prompt;

    public OpenAISSEEventSourceListener(SseEmitter sseEmitter) {
        super(sseEmitter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {

        log.info("流式输出返回值总共{}tokens", usage.getTotalTokens());
        log.info("OpenAI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        try {

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
            if (finishReason(chatChoice.getFinishReason())) {
                return;
            }
            String content = delta.getContent();
            if (log.isDebugEnabled()) {
                log.debug("content信息为:{}", content);
            }
            if (content == null) {
                return;
            }
            if ("".equals(content)) {
                delta.setContent("\n");
                content = "\n";
            }
            delta.setRole(Message.Role.ASSISTANT.getName());
            lastMessage += content;
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(delta)
                    .reconnectTime(3000));
        } catch (Exception e) {
            eventSource.cancel();
            throw e;
        }

    }





    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {

        try {
            SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                    .id(DONE_SIGNAL)
                    .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.ERROR)))
                    .reconnectTime(3000);

            if (t != null) {
                log.error("服务错误！{}", t.getMessage(), t);
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
                    sseEventBuilder = SseEmitter.event()
                            .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.ERROR.getCode(), commonError.msg())))
                            .reconnectTime(3000);
                }

            }

            sseEmitter.send(sseEventBuilder);
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
            sseEmitter.complete();
        }


    }

}
