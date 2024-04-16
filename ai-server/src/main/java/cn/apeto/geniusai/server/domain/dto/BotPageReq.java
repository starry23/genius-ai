package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import lombok.Data;

/**
 * @author wanmingyu
 * @create 2024/3/30 9:37 下午
 */
@Data
public class BotPageReq extends ReqPage {

    private String botName;
}
