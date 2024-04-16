package cn.apeto.geniusai.server.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.RSA;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.OpenApiMchConfigListDTO;
import cn.apeto.geniusai.server.entity.OpenApiMchConfig;
import cn.apeto.geniusai.server.service.OpenApiMchConfigService;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author wanmingyu
 * @create 2023/12/30 2:40 下午
 */
@Tag(name = "开放平台管理")
@Slf4j
@RequestMapping("/api/manager/open-api")
@RestController
@SaCheckLogin(type = StpManagerUtil.TYPE)
public class ManagerOpenApiController {

    @Autowired
    private OpenApiMchConfigService openApiMchConfigService;

    @Operation(summary = "列表")
    @GetMapping("/list")
    public ResponseResult<Page<OpenApiMchConfig>> list(OpenApiMchConfigListDTO openApiMchConfigListDTO) {
        String mchId = openApiMchConfigListDTO.getMchId();
        long current = openApiMchConfigListDTO.getCurrent();
        long size = openApiMchConfigListDTO.getSize();
        LambdaQueryWrapper<OpenApiMchConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(mchId), OpenApiMchConfig::getMchId, mchId);
        Page<OpenApiMchConfig> page = new Page<>(current, size);
        return ResponseResultGenerator.success(openApiMchConfigService.page(page, queryWrapper));
    }

    @Operation(summary = "保存或更新")
    @PostMapping("/saveOrUpdate")
    public ResponseResult<?> saveOrUpdate(@RequestBody OpenApiMchConfig openApiMchConfig) {
        String mchId = openApiMchConfig.getMchId();
        OpenApiMchConfig openApiMchConfigServiceByMchId = openApiMchConfigService.getByMchId(mchId);
        if (openApiMchConfig.getId() == null && openApiMchConfigServiceByMchId != null) {
            return ResponseResultGenerator.result(500, "商户号已存在");
        }
        openApiMchConfigService.saveOrUpdate(openApiMchConfig);
        return ResponseResultGenerator.success();

    }

    @Operation(summary = "删除")
    @DeleteMapping("/delete/{id}")
    public ResponseResult<?> delete(@PathVariable Long id) {
        openApiMchConfigService.removeById(id);
        return ResponseResultGenerator.success();
    }

            @Operation(summary = "生成公私钥")
    @PostMapping("/generateKeyPair")
    public ResponseResult<Map<String, String>> generateKeyPair() {
        RSA rsa = SecureUtil.rsa();
        rsa.setEncryptBlockSize(2048);
        rsa.setDecryptBlockSize(2048);
        rsa.getPrivateKey().getEncoded();
        String privateKeyBase64 = rsa.getPrivateKeyBase64();
        String publicKeyBase64 = rsa.getPublicKeyBase64();
        Map<String, String> map = MapUtil.<String, String>builder()
                .put("privateKey", privateKeyBase64)
                .put("publicKey", publicKeyBase64)
                .build();
        return ResponseResultGenerator.success(map);
    }

    @Operation(summary = "生成商户号")
    @PostMapping("/generateMchId")
    public ResponseResult<String> generateMchId() {
        return ResponseResultGenerator.success(DateUtil.format(DateUtil.date(), DatePattern.PURE_DATETIME_PATTERN));
    }

    @Operation(summary = "复制公私钥")
    @PostMapping("/copy")
    public ResponseResult<Map<String, String>> copy(@RequestBody OpenApiMchConfig openApiMchConfig) {
        RSA rsa = SecureUtil.rsa(openApiMchConfig.getPrivateKey(), openApiMchConfig.getPublicKey());

        String publicKey = Base64.encode(rsa.getPublicKey().getEncoded());
        String privateKey = Base64.encode(rsa.getPrivateKey().getEncoded());
        String publicStart = "-----BEGIN PUBLIC KEY-----\n";
        String publicEnd = "-----END PUBLIC KEY-----";

        String privateStart = "-----BEGIN PRIVATE KEY-----\n";
        String privateEnd = "----------END PRIVATE KEY-----";
        Map<String, String> map = MapUtil.<String, String>builder()
                .put("privateKey", privateStart+privateKey+privateEnd)
                .put("publicKey", publicStart + publicKey + publicEnd)
                .build();
        return ResponseResultGenerator.success(map);
    }
}
