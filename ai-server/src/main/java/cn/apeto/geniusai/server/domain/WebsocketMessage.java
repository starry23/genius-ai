package cn.apeto.geniusai.server.domain;

import cn.apeto.geniusai.sdk.entity.chat.Message;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gblhjh
 * @Date: 2023/08/04/12:18
 * @Description:
 */
@Data
public class WebsocketMessage {
    private String id;
    private Message message;

    public WebsocketMessage() {
    }

    public WebsocketMessage(String id, Message message) {
        this.id = id;
        this.message = message;
    }
}
