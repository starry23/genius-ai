package cn.apeto.geniusai.server.config.properties;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author apeto
 * @create 2023/4/13 16:52
 */
@Data
@Component
@ConfigurationProperties("ali-pay.conf")
public class AliPayProperties {

  @Schema(description = "是否启用")
  private Boolean enable = true;
  @Schema(description = "商户公钥")
  private String appPublicKey;
  @Schema(description = "商户私钥")
  private String appPrivateKey;
  @Schema(description = "支付宝公钥")
  private String aliPublicKey;
  @Schema(description = "appId")
  private String appId;
  @Schema(description = "是否强制开启当面付")
  private Boolean openFacePay = false;
}
