package cn.apeto.geniusai.server.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import cn.apeto.geniusai.server.config.properties.AliSmsProperties;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.service.sms.TencentSmsClient;
import cn.apeto.geniusai.server.service.sms.entity.TencentSendSmsRequest;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.CompletableFuture;

/**
 * @author apeto
 * @create 2023/4/17 14:03
 */
@Slf4j
public class SmsUtils {

  public static boolean sendSms (String code, String phoneNumbers) throws Exception, InterruptedException {
    SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
    AliSmsProperties aliSmsProperties = packageWebConfig.getAliSmsSetting();
    //配置项为空走本地腾讯云配置
    if(aliSmsProperties==null|| StringUtils.isEmpty(aliSmsProperties.getAccessKey())){
      TencentSmsClient smsClient = SpringUtil.getBean(TencentSmsClient.class);
      TencentSendSmsRequest smsRequest=new TencentSendSmsRequest();
      smsRequest.setTemplateParams(new String[]{code});
      smsRequest.setPhoneNumbers(phoneNumbers);
      return smsClient.sendSms(smsRequest).getCode()==200;
    }
    StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
        .accessKeyId(aliSmsProperties.getAccessKey())
        .accessKeySecret(aliSmsProperties.getAccessKeySecret())
        //.securityToken("<your-token>") // use STS token
        .build());

    // Configure the Client
    AsyncClient client = AsyncClient.builder()
        .region("cn-hangzhou") // Region ID
        //.httpClient(httpClient) // Use the configured HttpClient, otherwise use the default HttpClient (Apache HttpClient)
        .credentialsProvider(provider)
        //.serviceConfiguration(Configuration.create()) // Service-level configuration
        // Client-level configuration rewrite, can set Endpoint, Http request parameters, etc.
        .overrideConfiguration(
            ClientOverrideConfiguration.create()
                .setEndpointOverride("dysmsapi.aliyuncs.com")
            //.setConnectTimeout(Duration.ofSeconds(30))
        )
        .build();

    // Parameter settings for API request
    SendSmsRequest sendSmsRequest = SendSmsRequest.builder()
        .signName(aliSmsProperties.getSignName())
        .templateCode(aliSmsProperties.getTemplateCode())
        .phoneNumbers(phoneNumbers)
        .templateParam(JSONUtil.toJsonStr(MapUtil.builder("code", code).build()))
        // Request-level configuration rewrite, can set Http request parameters, etc.
        // .requestConfiguration(RequestConfiguration.create().setHttpHeaders(new HttpHeaders()))
        .build();

    // Asynchronously get the return value of the API request
    CompletableFuture<SendSmsResponse> response = client.sendSms(sendSmsRequest);
    // Synchronously get the return value of the API request
    SendSmsResponse resp = response.get();
    SendSmsResponseBody sendSmsResponseBody = resp.getBody();
    log.info("短信发送结果信息:{}",JSONUtil.toJsonStr(sendSmsResponseBody));
    boolean result = sendSmsResponseBody != null && sendSmsResponseBody.getCode().equals("OK");
    // Asynchronous processing of return values
        /*response.thenAccept(resp -> {
            System.out.println(new Gson().toJson(resp));
        }).exceptionally(throwable -> { // Handling exceptions
            System.out.println(throwable.getMessage());
            return null;
        });*/

    // Finally, close the client
    client.close();
    return result;
  }

}
