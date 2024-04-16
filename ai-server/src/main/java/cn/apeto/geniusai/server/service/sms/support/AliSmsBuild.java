package cn.apeto.geniusai.server.service.sms.support;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import cn.apeto.geniusai.server.config.properties.AliSmsProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AliSmsBuild {
    private static final Map<String, Client> MAP = new ConcurrentHashMap<>();

    private static Config buildAliConfig(AliSmsProperties aliProperties) {
        Config config = new Config()
                .setAccessKeyId(aliProperties.getAccessKey())
                .setAccessKeySecret(aliProperties.getAccessKeySecret());
        config.endpoint = "dysmsapi.aliyuncs.com";
        return config;
    }

    public static Client buildAliClient(AliSmsProperties smsProperties) {
        if (Objects.isNull(smsProperties) && Objects.isNull(smsProperties)) {
            log.warn("ali_client未进行配置，无法发送");
            return null;
        }
        Client aliClient = MAP.get("ali_client");
        if (!Objects.isNull(aliClient)) {
            return aliClient;
        }
        Config config = buildAliConfig(smsProperties);
        try {
            aliClient = new Client(config);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("ali_client构建失败");
        }
        MAP.put("ali_client", aliClient);
        return aliClient;
    }
}
