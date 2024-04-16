package cn.apeto.geniusai.sdk.entity.weixin;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/7/20 12:09
 */
@Data
public class RequestToken {

    private String grant_type;
    private String client_id;
    private String client_secret;
}
