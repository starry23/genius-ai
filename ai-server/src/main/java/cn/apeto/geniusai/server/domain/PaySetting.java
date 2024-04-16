package cn.apeto.geniusai.server.domain;

import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/16 1:26 上午
 */
@Schema(description = "PaySetting")
@Data
public class PaySetting {

    @Schema(description = "关闭支付公告")
    private String closePayNotice;

    @Schema(description = "支付宝支付配置")
    private AliPayProperties aliPayProperties;

    @Schema(description = "微信支付配置")
    private WechatPayConfig wechatPayConfig;
}
