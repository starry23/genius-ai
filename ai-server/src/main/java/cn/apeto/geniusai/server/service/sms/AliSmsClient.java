package cn.apeto.geniusai.server.service.sms;

import com.alibaba.fastjson.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.dysmsapi20170525.models.SendSmsResponseBody;
import cn.apeto.geniusai.server.config.properties.AliSmsProperties;
import cn.apeto.geniusai.server.service.sms.entity.AliSendSmsRequest;
import cn.apeto.geniusai.server.service.sms.entity.EasySmsResponse;
import cn.apeto.geniusai.server.service.sms.support.AliSmsBuild;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

;
@Slf4j
@Component
public class AliSmsClient {

    @Autowired
    private final AliSmsProperties smsAliProperties;

    public AliSmsClient(AliSmsProperties smsAliProperties) {
        this.smsAliProperties = smsAliProperties;
    }

    /**
     * 短信发送
     *
     * @param aliSendSmsRequest 信息发送主体
     * @return 消息返回体
     * @throws Exception 发送失败
     */
    public EasySmsResponse<SendSmsResponseBody> sendSms(AliSendSmsRequest aliSendSmsRequest) throws Exception {
        SendSmsResponse sendSmsResponse = Objects.requireNonNull(AliSmsBuild.buildAliClient(smsAliProperties)).sendSms(buildSendSmsRequest(aliSendSmsRequest));
        return EasySmsResponse.success(sendSmsResponse.getBody());
    }

    private SendSmsRequest buildSendSmsRequest(AliSendSmsRequest aliSendSmsRequest) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(aliSendSmsRequest.getPhoneNumbers())
                .setSignName(aliSendSmsRequest.getSignName()==null?smsAliProperties.getSignName():aliSendSmsRequest.getSignName())
                .setTemplateParam(JSON.toJSONString(aliSendSmsRequest.getTemplateParam()))
                .setOutId(aliSendSmsRequest.getOutId())
                .setTemplateCode(aliSendSmsRequest.getTemplateCode()==null?smsAliProperties.getTemplateCode():aliSendSmsRequest.getTemplateCode());
        return sendSmsRequest;
    }
}
