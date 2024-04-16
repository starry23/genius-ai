package cn.apeto.geniusai.server.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.MilvusVO;
import cn.apeto.geniusai.server.service.oss.CloudStorageConfig;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MilvusClientUtil;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author apeto
 * @create 2023/7/16 1:27 上午
 */
@Tag(name = "后台管理配置相关")
@Slf4j
@RestController
@RequestMapping("/api/manager/config")
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerConfigController {

    @Operation(summary = "支付配置")
    @GetMapping("/getPaySetting")
    public ResponseResult<PaySetting> getPaySetting() {
        return ResponseResultGenerator.success(CommonUtils.getPaySetting());
    }

    @Operation(summary = "支付配置")
    @PostMapping("/saveOrUpdatePaySetting")
    public ResponseResult<?> savePaySetting(@RequestBody PaySetting paySetting) {
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.PAY_SETTING.getKey(), JSONUtil.toJsonStr(paySetting));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "milvus向量库信息")
    @GetMapping("/milvus")
    public ResponseResult<MilvusVO> milvus() {
        return ResponseResultGenerator.success(JSONUtil.toBean(StringRedisUtils.get(SystemConstants.RedisKeyEnum.MILVUS.getKey()), MilvusVO.class));
    }

    @Operation(summary = "更新或保存milvus向量库")
    @PostMapping("/saveOrUpdateMilvus")
    public ResponseResult<?> saveOrUpdateMilvus(@RequestBody MilvusVO milvusVO) {
        MilvusClientUtil.setMilvusClient(milvusVO);
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.MILVUS.getKey(), JSONUtil.toJsonStr(milvusVO));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "修改milvus向量库密码")
    @PostMapping("/updatePassword")
    public ResponseResult<?> updatePassword(@RequestBody MilvusVO milvusVO) {
        MilvusClientUtil.updateCredential(milvusVO.getName(), milvusVO.getOldPassword(), milvusVO.getPassword());
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.MILVUS.getKey(), JSONUtil.toJsonStr(milvusVO));
        return ResponseResultGenerator.success();
    }


    @Operation(summary = "网站设置查询")
    @GetMapping("/getSetting")
    public ResponseResult<SaveSettingDTO> getSetting() {
        return ResponseResultGenerator.success(CommonUtils.getPackageWebConfig());
    }

    @Operation(summary = "网站设置")
    @PostMapping("/saveSetting")
    public ResponseResult<?> saveSetting(@RequestBody SaveSettingDTO saveSettingDTO) {
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.WEB_CONFIG.getKey(), JSONUtil.toJsonStr(saveSettingDTO));
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "网站OSS设置查询")
    @GetMapping("/getOssSetting")
    public ResponseResult<CloudStorageConfig> getOssSetting() {
        return ResponseResultGenerator.success(CommonUtils.getCloudStorageConfig());
    }

    @Operation(summary = "网站OSS设置")
    @PostMapping("/saveOssSetting")
    public ResponseResult<?> saveOssSetting(@RequestBody CloudStorageConfig cloudStorageConfig) {
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.OSS_CONFIG.getKey(), JSONUtil.toJsonStr(cloudStorageConfig));
        OSSFactory.destroy();
        return ResponseResultGenerator.success();
    }


    @Operation(summary = "获取chat配置")
    @GetMapping("/chatConfig")
    public ResponseResult<ChatConfigEntity> chatConfig() {
        return ResponseResultGenerator.success(CommonUtils.getChatConfig());
    }

    @Operation(summary = "chat配置添加或修改")
    @PostMapping("/saveOrUpdateChatConfig")
    public ResponseResult<?> saveOrUpdateChatConfig(@RequestBody ChatConfigEntity chatConfig) {

        ChatConfigEntity.Entity entity3 = chatConfig.getEntity3();
        ChatConfigEntity.Entity entity4 = chatConfig.getEntity4();
        ChatConfigEntity.Entity dalle = chatConfig.getDalle();
        ChatConfigEntity.Entity gptsEntity = chatConfig.getGptsEntity();

        entity3.setModelType(ChatCompletion.ModelType.GPT3);
        entity4.setModelType(ChatCompletion.ModelType.GPT4);
        dalle.setModelType(ChatCompletion.ModelType.DALL_E);
        gptsEntity.setModelType(ChatCompletion.ModelType.GPTS);


        Integer contextLimit = entity3.getContextLimit();
        Integer contextSplit = entity3.getContextSplit();
        if (contextLimit * contextSplit > entity3.getMaxTokens()) {
            return ResponseResultGenerator.error("GPT3 上下文配置超过最大token数");
        }

        Integer contextLimit4 = entity4.getContextLimit();
        Integer contextSplit4 = entity4.getContextSplit();
        if (contextLimit4 * contextSplit4 > entity4.getMaxTokens()) {
            return ResponseResultGenerator.error("GPT4 上下文配置超过最大token数");
        }
        log.info("插入chatConfig:{}",JSONUtil.toJsonStr(chatConfig));
        StringRedisUtils.set(SystemConstants.RedisKeyEnum.CHAT_CONFIG.getKey(), JSONUtil.toJsonStr(chatConfig));
        return ResponseResultGenerator.success();
    }
}
