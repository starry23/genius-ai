package cn.apeto.geniusai.sdk.entity.weixin;

import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/7/20 12:09
 */
@Data
public class ResponseToken {

    private String refresh_token;
    private Long expires_in;
    private String session_key;
    private String access_token;
    private String scope;
    private String session_secret;
}
