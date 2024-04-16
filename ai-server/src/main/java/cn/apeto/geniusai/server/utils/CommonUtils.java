package cn.apeto.geniusai.server.utils;

import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.chat.Message;
import cn.apeto.geniusai.sdk.utils.TikTokensUtil;
import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.config.properties.EmailConfigProperties;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MyMessage;
import cn.apeto.geniusai.server.domain.PaySetting;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.service.oss.CloudStorageConfig;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/4/9 3:39 下午
 */
@Slf4j
public class CommonUtils {

    public static SymmetricCrypto inviteSymmetricCrypto() {

        String key = SpringUtil.getProperty("activity.invite-config.key");
        return new SymmetricCrypto(SymmetricAlgorithm.AES, key.getBytes(StandardCharsets.UTF_8));
    }

    public static String md5Salt(String password) {
        return SecureUtil.md5(password + "ai");
    }

    public static void sendEmail(String from, String subject, String msg) {
        try {
            SaveSettingDTO packageWebConfig = getPackageWebConfig();
            EmailConfigProperties emailConfigProperties = packageWebConfig.getEmailConfigProperties();
            MailAccount account = new MailAccount();
            account.setHost(emailConfigProperties.getHost());
            account.setPort(emailConfigProperties.getPort());
            account.setAuth(emailConfigProperties.getAuth());
            account.setFrom(emailConfigProperties.getFrom());
            account.setUser(emailConfigProperties.getUser());
            account.setPass(emailConfigProperties.getPass());
            account.setSslEnable(true);
            MailUtil.send(account, CollUtil.newArrayList(StrUtil.isBlank(from) ? emailConfigProperties.getFrom() : from), subject, msg, false);
        } catch (Exception e) {
            log.error("发送邮件异常", e);
        }

    }

    public static SaveSettingDTO getPackageWebConfig() {

        SaveSettingDTO saveSettingDTO;
        String confJson = StringRedisUtils.get(RedisKeyEnum.WEB_CONFIG.getKey());
        if (StrUtil.isBlank(confJson)) {
            saveSettingDTO = new SaveSettingDTO();
        } else {
            saveSettingDTO = JSONUtil.toBean(confJson, SaveSettingDTO.class);
        }

        String domainUrl = SpringUtil.getApplicationContext().getEnvironment().getProperty("appServer.domain");

        if (StrUtil.isBlank(saveSettingDTO.getWebName())) {
            saveSettingDTO.setWebName("Genius AI");
        }
        if (StrUtil.isBlank(saveSettingDTO.getIconUrl())) {
            saveSettingDTO.setIconUrl("http://chat.apeto.cn/imgss/20240227/041ac29693b548d6bb30d66242caf7b7.png");
        }

        if (StrUtil.isBlank(saveSettingDTO.getSubTitle())) {
            saveSettingDTO.setSubTitle("老铁~科技改变成活!");
        }

        if (StrUtil.isBlank(saveSettingDTO.getSysTheme())) {
            saveSettingDTO.setSysTheme("light");
        }

        if (StrUtil.isBlank(saveSettingDTO.getDomain())) {
            saveSettingDTO.setSubTitle(domainUrl);
        }

        if (CollUtil.isEmpty(saveSettingDTO.getLeftMenuIds())) {
            List<Integer> values = Arrays.stream(Constants.LeftMenuTypeEnum.values()).map(Constants.LeftMenuTypeEnum::getType).collect(Collectors.toList());
            saveSettingDTO.setLeftMenuIds(values);
        }

        AliPayProperties aliPayProperties = saveSettingDTO.getAliPayProperties();
        if (aliPayProperties == null) {
            aliPayProperties = SpringUtil.getBean(AliPayProperties.class);
            saveSettingDTO.setAliPayProperties(aliPayProperties);
        }

        EmailConfigProperties emailConfigProperties = saveSettingDTO.getEmailConfigProperties();
        if (emailConfigProperties == null) {
            emailConfigProperties = SpringUtil.getBean(EmailConfigProperties.class);
            saveSettingDTO.setEmailConfigProperties(emailConfigProperties);
        }
        saveSettingDTO.setWebVersion(Constants.VERSION);
        return saveSettingDTO;
    }

    /**
     * 获取云存储配置
     *
     * @return
     */
    public static CloudStorageConfig getCloudStorageConfig() {
        CloudStorageConfig cloudStorageConfig = new CloudStorageConfig();
        String confJson = StringRedisUtils.get(RedisKeyEnum.OSS_CONFIG.getKey());
        if (StringUtils.isNotBlank(confJson)) {
            cloudStorageConfig = JSONUtil.toBean(confJson, CloudStorageConfig.class);
        }
        if(StrUtil.isBlank(cloudStorageConfig.getLocalRootPath())){
            SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
            try {
                URL url = new URL(packageWebConfig.getDomain());
                cloudStorageConfig.setLocalRootPath("/www/wwwroot/"+ url.getHost()+"/base_web/");
            }catch (Exception e){
                log.warn("配置文件domain域名解析错误");
            }

        }
        return cloudStorageConfig;
    }

    public static Map<String, String> convertToMap(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>(32);
        Map<String, String[]> requestParams = request.getParameterMap();
        for (String name : requestParams.keySet()) {
            String[] values = requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
        }
        return params;
    }

    /**
     * 获取返佣等级
     */
    public static String getRebateLevel(SaveSettingDTO.ActivitySetting.RebateConfig rebateConfig, Integer count) {
        if (!rebateConfig.getEnable()) {
            return "当前无返佣活动";
        }
        SaveSettingDTO.ActivitySetting.Rebate rebate1 = rebateConfig.getRebate1();
        SaveSettingDTO.ActivitySetting.Rebate rebate2 = rebateConfig.getRebate2();
        SaveSettingDTO.ActivitySetting.Rebate rebate3 = rebateConfig.getRebate3();
        String rebateLevel;

        if (count < rebate1.getNum()) {
            rebateLevel = "1级" + rebate1.getRate() + "%";
        } else if (count < rebate2.getNum()) {
            rebateLevel = "2级" + rebate2.getRate() + "%";
        } else {
            rebateLevel = "3级" + rebate3.getRate() + "%";
        }
        return rebateLevel;
    }


//    public static ChatEntity getChatConfigEntity(Constants.ProductTypeEnum productTypeEnum) {
//
//
//        ChatConfigEntity chatConfigEntity = getChatConfig();
//
//        if (chatConfigEntity == null) {
//            throw new ServiceException(5001, "请先置聊天信息");
//        }
//
//        if (productTypeEnum.equals(Constants.ProductTypeEnum.GPT3_5)) {
//            return chatConfigEntity.getEntity3();
//        }
//        if (productTypeEnum.equals(Constants.ProductTypeEnum.GPT4)) {
//            return chatConfigEntity.getEntity4();
//        }
//        if (productTypeEnum.equals(Constants.ProductTypeEnum.WXQF)) {
//            return chatConfigEntity.getWxqfEntity();
//        }
//        return chatConfigEntity.getEntity3();
//    }

    public static ChatConfigEntity getChatConfig() {
        String confJson = StringRedisUtils.get(RedisKeyEnum.CHAT_CONFIG.getKey());
        return JSONUtil.toBean(confJson, ChatConfigEntity.class);
    }

    public static PaySetting getPaySetting() {
        String confJson = StringRedisUtils.get(RedisKeyEnum.PAY_SETTING.getKey());
        return JSONUtil.toBean(confJson, PaySetting.class);
    }

    public static ChatCompletion packageCompletion(List<MyMessage> messages, ChatConfigEntity.Entity chatConfig) {
        List<Message> messageList = messages.stream().map(myMessage -> new Message(myMessage.getRole(), myMessage.getContent(), myMessage.getName())).collect(Collectors.toList());
        Integer maxToken = chatConfig.getMaxTokens();
        String model = chatConfig.getModel();
        int currentToken = maxToken;
        if (CollUtil.isNotEmpty(messageList)) {
            currentToken = maxToken - TikTokensUtil.tokens(model, messageList.stream().map(Message::getContent).collect(Collectors.joining()));
        }

        return ChatCompletion
                .builder()
                .modelType(ChatCompletion.ModelType.GPT3)
                .messages(messageList)
                .temperature(chatConfig.getTemperature())
                .maxTokens(currentToken)
                .frequencyPenalty(chatConfig.getFrequencyPenalty())
                .presencePenalty(chatConfig.getPresencePenalty())
                .model(model)
                .build();
    }
}
