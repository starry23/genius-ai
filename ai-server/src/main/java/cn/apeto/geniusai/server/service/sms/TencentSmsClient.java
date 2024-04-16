package cn.apeto.geniusai.server.service.sms;

import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import cn.apeto.geniusai.server.service.sms.entity.EasySmsResponse;
import cn.apeto.geniusai.server.service.sms.entity.TencentSendSmsRequest;
import cn.apeto.geniusai.server.config.properties.TencentSmsProperties;
import cn.apeto.geniusai.server.service.sms.support.TencentSmsBuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class TencentSmsClient {

    @Autowired
    private final TencentSmsProperties tencentSmsProperties;

    public TencentSmsClient(TencentSmsProperties tencentSmsProperties) {
        this.tencentSmsProperties = tencentSmsProperties;
    }

    /**
     * 同步消息发送
     *
     * @param tencentSendSmsRequest 信息发送主体
     * @return 消息返回体
     * @throws Exception 发送失败
     */
    public EasySmsResponse<SendSmsResponse> sendSms(TencentSendSmsRequest tencentSendSmsRequest) throws Exception {
        SendSmsResponse smsResponse = Objects.requireNonNull(TencentSmsBuild.buildTencentClient(tencentSmsProperties)).SendSms(buildSendSmsRequest(tencentSendSmsRequest));
        return EasySmsResponse.success(smsResponse);
    }


    private SendSmsRequest buildSendSmsRequest(TencentSendSmsRequest tencentSendSmsRequest) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setSmsSdkAppId(tencentSendSmsRequest.getSdkAppId()==null? tencentSmsProperties.getSdkAppId():tencentSendSmsRequest.getSdkAppId());
        sendSmsRequest.setSignName(tencentSendSmsRequest.getSignName()==null? tencentSmsProperties.getSignName():tencentSendSmsRequest.getSignName());
        sendSmsRequest.setTemplateId(tencentSendSmsRequest.getTemplateId()==null? tencentSmsProperties.getTemplateId():tencentSendSmsRequest.getTemplateId());
        sendSmsRequest.setTemplateParamSet(tencentSendSmsRequest.getTemplateParams());
        sendSmsRequest.setPhoneNumberSet(buildPhoneNumber(tencentSendSmsRequest.getPhoneNumbers()));
        sendSmsRequest.setSessionContext(tencentSendSmsRequest.getSessionContext());
        sendSmsRequest.setExtendCode(tencentSendSmsRequest.getExtendCode());
        sendSmsRequest.setSenderId(tencentSendSmsRequest.getSenderId());
        return sendSmsRequest;
    }

    private String[] buildPhoneNumber(String phoneNumbers) {
        String[] phones = phoneNumbers.split(",");
        for (String phone : phones) {
            if (!phone.startsWith("+")) {
                phone = "+86" + phone;
            }
        }
        return phones;
    }
}
