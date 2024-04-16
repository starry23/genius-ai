package cn.apeto.geniusai.server.config;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.entity.KeyInfo;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.function.KeyStrategy;
import cn.apeto.geniusai.server.domain.ChatConfigEntity;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author apeto
 * @create 2023/5/16 14:57
 */
@Slf4j
@Component
public class MyKeyStrategy implements KeyStrategy {

    @Override
    public KeyInfo strategy(ChatCompletion.ModelType modelType,String modelName) {
        return RandomUtil.randomEle(getKeys(modelType,modelName));
    }

    @Override
    public List<KeyInfo> getKeys(ChatCompletion.ModelType modelType,String modelName) {
        ChatConfigEntity chatConfigEntity = JSONUtil.toBean(StringRedisUtils.get(RedisKeyEnum.CHAT_CONFIG.getKey()), ChatConfigEntity.class);

        if (ChatCompletion.ModelType.GPT4.equals(modelType)) {
            ChatConfigEntity.Entity entity4 = chatConfigEntity.getEntity4();
            return entity4.getApiKeys().stream().map(key -> {
                KeyInfo keyInfo = new KeyInfo();
                keyInfo.setKey(key);
                keyInfo.setApiHost(entity4.getDomain());
                return keyInfo;
            }).collect(Collectors.toList());
        }

        if(ChatCompletion.ModelType.DALL_E.equals(modelType)){
            ChatConfigEntity.Entity dalle = chatConfigEntity.getDalle();
            return dalle.getApiKeys().stream().map(key -> {
                KeyInfo keyInfo = new KeyInfo();
                keyInfo.setKey(key);
                keyInfo.setApiHost(dalle.getDomain());
                return keyInfo;
            }).collect(Collectors.toList());
        }

        if(ChatCompletion.ModelType.GPTS.equals(modelType)){
            ChatConfigEntity.Entity gptsEntity = chatConfigEntity.getGptsEntity();
            return gptsEntity.getApiKeys().stream().map(key -> {
                KeyInfo keyInfo = new KeyInfo();
                keyInfo.setKey(key);
                keyInfo.setApiHost(gptsEntity.getDomain());
                return keyInfo;
            }).collect(Collectors.toList());
        }

        ChatConfigEntity.Entity entity3 = chatConfigEntity.getEntity3();
        return entity3.getApiKeys().stream().map(key -> {
            KeyInfo keyInfo = new KeyInfo();
            keyInfo.setKey(key);
            keyInfo.setApiHost(entity3.getDomain());
            return keyInfo;
        }).collect(Collectors.toList());
    }

    @Override
    public void keysWarring() {
        log.error("所有key都已失效~请更换");
        CommonUtils.sendEmail(null, "服务异常", "所有key都已失效~请及时更换");
    }

    @Override
    public void removeErrorKey(String key, String errorMessage) {
        log.error("当前key:{} 已失效 自动删除中~", key);
        CommonUtils.sendEmail(null, "服务报警", StrUtil.format("当前key:{} 已失效 自动删除中~ 异常信息:{}", key, errorMessage));

        // 临时解决方案
        ChatConfigEntity chatConfig = CommonUtils.getChatConfig();
        ChatConfigEntity.Entity chatConfig3 = chatConfig.getEntity3();
        ChatConfigEntity.Entity chatConfig4 = chatConfig.getEntity4();
        chatConfig4.getApiKeys().remove(key);
        chatConfig3.getApiKeys().remove(key);
        chatConfig.setEntity3(chatConfig3);
        chatConfig.setEntity4(chatConfig4);
        StringRedisUtils.set(RedisKeyEnum.CHAT_CONFIG.getKey(), JSONUtil.toJsonStr(chatConfig));
    }
}
