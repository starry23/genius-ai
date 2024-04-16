package cn.apeto.geniusai.server.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
@Data
@Component
@ConfigurationProperties(prefix = "tencent-config.sms")
public class TencentSmsProperties {
    private String secretId;
    private String secretKey;
    private String sdkAppId;
    private String signName;
    private String templateId;
}
