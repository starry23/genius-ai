package cn.apeto.geniusai.server.core;

import org.springframework.web.socket.WebSocketSession;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/06/13:10
 * @Description:
 */
public abstract class AbstractWebSocketChatService {

    public abstract void doChat(WebSocketSession session, String reqId, Long userId, String prompt, Boolean internet);
}
