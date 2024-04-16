package cn.apeto.geniusai.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/28 12:54
 */
@Schema(description = "WechatPayConfig")
@Data
public class WechatPayConfig {

    @Schema(description = "是否启用")
    private Boolean enable = false;
    @Schema(description = "appId")
    private String appId;
    @Schema(description = "商户号")
    private String mchId;
    @Schema(description = "APIv2密钥")
    private String partnerKey;
    @Schema(description = "h5WapUrl")
    private String h5WapUrl;
}
