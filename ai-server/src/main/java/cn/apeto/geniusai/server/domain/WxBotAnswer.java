package cn.apeto.geniusai.server.domain;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2024/1/29 5:37 下午
 */
@Data
public class WxBotAnswer {

    private String createTime;
    private String prompt;
    private String openId;
    private String channel;
}
