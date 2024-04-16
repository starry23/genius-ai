package cn.apeto.geniusai.server.listener;

import cn.apeto.geniusai.server.domain.Usage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * @author apeto
 * @create 2023/7/27 12:27
 */
@Slf4j
public class BaseEventSourceListener extends EventSourceListener {

    protected final SseEmitter sseEmitter;

    protected String lastMessage = "";

    protected volatile Usage usage = new Usage();

    protected static final String DONE_SIGNAL = "[DONE]";

    /**
     * Called when all new message are received.
     *
     * @param message the new message
     */
    @Setter
    @Getter
    protected BiConsumer<String, Usage> onComplete = (msg, token) -> {

    };

    @Override
    public void onClosed(EventSource eventSource) {

        try {
            log.info("流式输出返回值总共{}tokens", usage.getTotalTokens());
            log.info("OpenAI关闭sse连接...");
            onComplete.accept(lastMessage, usage);
            sseEmitter.send(SseEmitter.event()
                    .id("")
                    .data("DONE")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("关闭异常", e);
        }
    }

    public BaseEventSourceListener(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
    }

    protected synchronized void updateUsage(long completionTokens, long promptTokens, long totalTokens) {
        this.usage.setCompletionTokens(completionTokens);
        this.usage.setPromptTokens(promptTokens);
        this.usage.setTotalTokens(totalTokens);
    }


    public boolean finishReason(String finishReason) throws IOException {
        if (!"stop".equals(finishReason)) {
           return false;
        }
//        onComplete.accept(lastMessage, usage);
//        sseEmitter.send(SseEmitter.event()
//                .data("DONE")
//                .reconnectTime(3000));
//        // 传输完成后自动关闭sse
//        sseEmitter.complete();
        return true;
    }

}
