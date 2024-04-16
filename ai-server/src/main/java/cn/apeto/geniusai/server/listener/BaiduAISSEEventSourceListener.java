package cn.apeto.geniusai.server.listener;

import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.entity.weixin.BaiduCompletionResponse;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
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

import java.io.IOException;
import java.util.Objects;

/**
 * 描述：BAIDU_AIEventSourceListener
 *
 * @author warape
 * @date 2023-02-22
 */
@Slf4j
public class BaiduAISSEEventSourceListener extends BaseEventSourceListener {


    public BaiduAISSEEventSourceListener(SseEmitter sseEmitter) {
        super(sseEmitter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("BAIDU_AI建立sse连接...");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            BaiduCompletionResponse baiduCompletionResponse = mapper.readValue(data, BaiduCompletionResponse.class);// 读取Json
            String result = baiduCompletionResponse.getResult();
            MyMessage myMessage = new MyMessage();
            myMessage.setContent(result);
            myMessage.setRole(Message.Role.ASSISTANT.getName());
            BaiduCompletionResponse.Usage responseUsage = baiduCompletionResponse.getUsage();
            updateUsage(responseUsage.getCompletion_tokens(),responseUsage.getPrompt_tokens(),responseUsage.getTotal_tokens());
            if (baiduCompletionResponse.getIs_end()) {
                if(StrUtil.isNotBlank(result)){
                    lastMessage += result;
                    sseEmitter.send(SseEmitter.event()
                            .id(baiduCompletionResponse.getId())
                            .data(myMessage)
                            .reconnectTime(3000));
                }
                // 执行完毕
                onComplete.accept(lastMessage, super.usage);
                sseEmitter.send(SseEmitter.event()
                        .id(id)
                        .data("DONE")
                        .reconnectTime(3000));
                // 传输完成后自动关闭sse
                sseEmitter.complete();
                return;
            }
            lastMessage += result;
            sseEmitter.send(SseEmitter.event()
                    .id(baiduCompletionResponse.getId())
                    .data(myMessage)
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("onEvent", e);
            eventSource.cancel();
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("流式输出返回值总共{}tokens", usage.getTotalTokens());
        log.info("BAIDU_AI关闭sse连接...");
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
                if (t instanceof IOException) {
                    log.error("IOException网络错误！{}", t.getMessage());
                } else {
                    log.error("服务错误！{}", t.getMessage());
                }
            }

            if (Objects.isNull(response)) {
                log.error("BAIDU_AI  sse连接异常", t);
                return;
            }

            ResponseBody body = response.body();
            if (Objects.nonNull(body)) {
                log.error("body error:{}", body.string());
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
