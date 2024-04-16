package cn.apeto.geniusai.server.listener;

import cn.apeto.geniusai.server.domain.Usage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.sse.EventSourceListener;
import org.springframework.web.socket.WebSocketSession;

import java.util.function.BiConsumer;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/04/11:34
 * @Description:
 */

@Slf4j
public class BaseWebSocketSourceListener extends EventSourceListener {


    // 最新消息
    protected String lastMessage = "";

    // token计算
    protected volatile Usage usage = new Usage();

    // 结束标识
    protected static final String DONE_SIGNAL = "[DONE]";


    // 会话
    public WebSocketSession session;

    public BaseWebSocketSourceListener(WebSocketSession session) {
        this.session = session;
    }

    /**
     * Called when all new message are received.
     *  抽象回调
     * @param message the new message
     */
    @Setter
    @Getter
    protected BiConsumer<String, Usage> onComplete = (msg, token) -> {

    };

    protected synchronized void updateUsage(long completionTokens, long promptTokens, long totalTokens) {
        this.usage.setCompletionTokens(completionTokens);
        this.usage.setPromptTokens(promptTokens);
        this.usage.setTotalTokens(totalTokens);
    }
}

