package cn.apeto.geniusai.server.config;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gblhjh
 * @Date: 2023/08/04/12:03
 * @Description:
 */

import cn.apeto.geniusai.server.websocket.ChatWebSocketHandler;
import cn.apeto.geniusai.server.websocket.ChatWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;
    // 注册 WebSocket 处理器
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                // WebSocket 连接处理器
                .addHandler(chatWebSocketHandler, "/api/ws-genius")
                // WebSocket 拦截器
                .addInterceptors(new ChatWebSocketInterceptor())
                // 允许跨域
                .setAllowedOriginPatterns("*")
                .withSockJS()
                // 二进制消息的最大小
                .setStreamBytesLimit(512 * 1024)
                // HTTP消息的缓存大小(条)
                .setHttpMessageCacheSize(1000)
                // 设置了心跳消息的发送频率 毫秒
                .setHeartbeatTime(15 * 1000)
                // 断开连接的延迟时间
                .setDisconnectDelay(60 * 1000);

    }

}

