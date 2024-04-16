package cn.apeto.geniusai.server.listener;

import cn.apeto.geniusai.sdk.entity.sparkEntiy.*;
import cn.apeto.geniusai.sdk.sse.ChatListener;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * @author wanmingyu
 * @create 2023/9/26 11:12
 */
@Slf4j
public class SparkDeskSSEChatListener extends ChatListener {

    @Getter
    private final BaseEventSourceListener baseEventSourceListener;

    public SparkDeskSSEChatListener(AIChatRequest aiChatRequest, SseEmitter sseEmitter) {
        super(aiChatRequest);
        this.baseEventSourceListener = new BaseEventSourceListener(sseEmitter);

    }



    @SneakyThrows
    @Override
    public void onChatError(AIChatResponse aiChatResponse) {
        if (StrUtil.isNotBlank(baseEventSourceListener.lastMessage)) {
            baseEventSourceListener.onComplete.accept(baseEventSourceListener.lastMessage, baseEventSourceListener.usage);
        }
        String message = aiChatResponse.getHeader().getMessage();
        log.error("星火onChatError:{}", message);
        SseEmitter.SseEventBuilder sseEventBuilder = SseEmitter.event()
                .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.ERROR)))
                .reconnectTime(3000);
        baseEventSourceListener.sseEmitter.send(sseEventBuilder);
    }

    @SneakyThrows
    @Override
    public void onChatOutput(AIChatResponse aiChatResponse) {
        String sid = aiChatResponse.getHeader().getSid();
        OutPayload payload = aiChatResponse.getPayload();
        Text text = payload.getChoices().getText().get(0);
        String content = text.getContent();
        baseEventSourceListener.lastMessage += content;
        baseEventSourceListener.sseEmitter.send(SseEmitter.event()
                .id(sid)
                .data(text)
                .reconnectTime(3000));
    }

    @SneakyThrows
    @Override
    public void onChatEnd() {
        baseEventSourceListener.sseEmitter.send(SseEmitter.event()
                .id(IdUtil.fastUUID())
                .data("DONE")
                .reconnectTime(3000));
        // 传输完成后自动关闭sse
        baseEventSourceListener.sseEmitter.complete();
    }

    @Override
    public void onChatToken(Usage usage) {
        Usage.Text text = usage.getText();
        cn.apeto.geniusai.server.domain.Usage u = new cn.apeto.geniusai.server.domain.Usage();
        u.setPromptTokens(text.getPromptTokens().longValue());
        u.setCompletionTokens(text.getCompletionTokens().longValue());
        u.setTotalTokens(text.getTotalTokens().longValue());
        baseEventSourceListener.onComplete.accept(baseEventSourceListener.lastMessage, u);
    }
}
