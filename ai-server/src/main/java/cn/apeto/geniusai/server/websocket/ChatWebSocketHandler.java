package cn.apeto.geniusai.server.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.core.KnowledgeShareServiceImpl;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.entity.KnowledgeChatBinding;
import cn.apeto.geniusai.server.entity.ShareItem;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.exception.SseEmitterException;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.KnowledgeChatBindingService;
import cn.apeto.geniusai.server.service.ShareItemService;
import cn.apeto.geniusai.server.utils.BaiduUtils;
import cn.apeto.geniusai.server.utils.CommonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author apeto
 * @create 2023/8/5 6:21 下午
 */
@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {


    /**
     * 用来存放每个客户端对应的WebSocketServer对象
     */
    private static final ConcurrentHashMap<String, Info> webSocketMap = new ConcurrentHashMap<>();


    @Autowired
    private KnowledgeShareServiceImpl knowledgeShareService;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private KnowledgeChatBindingService knowledgeChatBindingService;

    @Autowired
    private ShareItemService shareItemService;

    // 连接数
    private static final AtomicInteger onlineCount = new AtomicInteger(0);


    // 监听：连接开启
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws IOException {

        String sessionId = session.getId();
        String uuid = session.getAttributes().get("uuid").toString();
        Info info = new Info();
        info.setUuid(uuid);
        info.setSession(session);
        webSocketMap.put(sessionId, info);
        addOnlineCount();

        log.info("客户端连接开启成功: sessionId:{} 总连接数:{} 缓存map数量:{}", sessionId, getOnlineCount(), webSocketMap.size());

    }

    // 监听：连接关闭
    @Override
    public void afterConnectionClosed(WebSocketSession session, @NotNull CloseStatus status) {
        log.info("连接关闭 status:{} 总连接数:{}  缓存map数量:{}", status, getOnlineCount(), webSocketMap.size());
        webSocketMap.remove(session.getId());
        subOnlineCount();
    }

    @Override
    public void handleTransportError(@NotNull WebSocketSession session, @NotNull Throwable exception) throws Exception {
        log.error("连接异常", exception);
    }

    // 收到消息
    @Override
    public void handleTextMessage(@NotNull WebSocketSession session, TextMessage mes) throws IOException {

        try {
            String prompt = mes.getPayload();
            if (StrUtil.isBlank(prompt)) {
                throw new SseEmitterException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
            }


            String sessionId = session.getId();
            Info info = webSocketMap.get(sessionId);
            String uuid = info.getUuid();
            ShareItem shareItem = shareItemService.getByUuid(uuid);
            if (Objects.isNull(shareItem) || shareItem.getIsEnable().equals(0)) {
                session.sendMessage(new TextMessage("您访问你的链接无效"));
                return;
            }
            Long itemId = shareItem.getItemId();
            KnowledgeChatBinding knowledgeChatBinding = knowledgeChatBindingService.getByKnowledgeId(itemId);
            String reqId = knowledgeChatBinding.getChatLogReq();
            Long userId = shareItem.getUserId();

            // 2 余额查询
            exchangeCardDetailService.checkConsume(userId, Constants.ProductTypeEnum.GPT3_5.getType(), (long) prompt.length());

            // 3 百度过滤
            SaveSettingDTO settingDTO = CommonUtils.getPackageWebConfig();
            SaveSettingDTO.BaiduSetting baiduSetting = settingDTO.getBaiduSetting();
            if (baiduSetting.getTextEnable() && !BaiduUtils.baiduTextCensor(prompt)) {
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.SENSITIVE))));
            }

            knowledgeShareService.doChat(session, reqId, userId, prompt, false);
        } catch (ServiceException e) {
            log.error("handleTextMessage ServiceException:{}", e.getMessage());
            session.sendMessage(new TextMessage(JSONUtil.toJsonStr(ResponseResultGenerator.result(e.getCode(), e.getMessage()))));
        } catch (Exception e) {
            log.error("handleTextMessage Exception", e);
            session.sendMessage(new TextMessage(JSONUtil.toJsonStr(ResponseResultGenerator.error())));
        }

    }

    public static synchronized int getOnlineCount() {
        return onlineCount.get();
    }

    public static synchronized void addOnlineCount() {
        onlineCount.incrementAndGet();
    }

    public static synchronized void subOnlineCount() {
        onlineCount.decrementAndGet();
    }

    @Data
    public static class Info {
        //        private String reqId;
        private String uuid;
        //        private Long userId;
//        private Boolean isEnable = true;
        private WebSocketSession session;
    }

}
