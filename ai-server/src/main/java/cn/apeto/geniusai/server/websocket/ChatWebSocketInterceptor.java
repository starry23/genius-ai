package cn.apeto.geniusai.server.websocket;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * @author apeto
 * @create 2023/8/5 6:22 下午
 */
@Slf4j
public class ChatWebSocketInterceptor implements HandshakeInterceptor {

	// 握手之前触发 (return true 才会握手成功 )
	@Override
	public boolean beforeHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler handler,
								   @NotNull Map<String, Object> attr) {
//		if(!StpUtil.isLogin()) {
//			log.error("未授权登录");
//			return false;
//		}
		ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
		String uuid = servletRequest.getServletRequest().getParameter("uuid");
		if(StrUtil.isBlank(uuid)){
			log.warn("reqId为空 不能链接socket");
			return false;
		}
		attr.put("uuid",uuid);
		return true;
	}

	// 握手之后触发
	@Override
	public void afterHandshake(@NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response, @NotNull WebSocketHandler wsHandler,
							   Exception exception) {
	}

}
