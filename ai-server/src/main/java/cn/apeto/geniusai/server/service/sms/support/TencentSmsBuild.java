package cn.apeto.geniusai.server.service.sms.support;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import cn.apeto.geniusai.server.config.properties.TencentSmsProperties;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TencentSmsBuild {

    private static final Map<String, SmsClient> MAP = new ConcurrentHashMap<>();

    private static SmsClient buildTencentConfig(TencentSmsProperties tencentProperties) {
        Credential cred = new Credential(tencentProperties.getSecretId(), tencentProperties.getSecretKey());
        ClientProfile clientProfile = new ClientProfile();
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setReqMethod("POST");
        httpProfile.setConnTimeout(60);
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        clientProfile.setHttpProfile(httpProfile);
        return new SmsClient(cred, "ap-guangzhou", clientProfile);
    }

    public static SmsClient buildTencentClient(TencentSmsProperties smsProperties) {
        if (Objects.isNull(smsProperties) && Objects.isNull(smsProperties)) {
            log.warn("tencent_client未进行配置，无法发送");
            return null;
        }
        SmsClient tencentClient = MAP.get("tencent_client");
        if (!Objects.isNull(tencentClient)) {
            return tencentClient;
        }
        SmsClient smsClient = buildTencentConfig(smsProperties);
        MAP.put("tencent_client", smsClient);
        return smsClient;
    }
}
