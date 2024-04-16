package cn.apeto.geniusai.server.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.MJManagerListDTO;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.res.mj.TenantInfoRes;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.service.MjImageInfoService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.RegexUtils;
import cn.apeto.geniusai.server.utils.StpManagerUtil;
import cn.apeto.geniusai.server.utils.ApetoMjUtils;
import cn.apeto.geniusai.server.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author apeto
 * @create 2023/6/24 4:24 下午
 */
@Tag(name = "后台管理系统")
@Slf4j
@RestController
@SaCheckLogin(type = StpManagerUtil.TYPE)
@RequestMapping("/api/mj/manager")
public class MjManagerController {

    @Autowired
    private MjImageInfoService mjImageInfoService;

    @Operation(summary = "mj管理列表")
    @GetMapping("/list")
    public ResponseResult<?> list(MJManagerListDTO mjManagerListDTO) {
        Long userId = mjManagerListDTO.getUserId();
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(userId != null, MjImageInfo::getUserId, userId);
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<MjImageInfo> page = mjImageInfoService.page(new Page<>(mjManagerListDTO.getCurrent(), mjManagerListDTO.getSize()), queryWrapper);
        IPage<MjImageInfo> convert = page.convert(mjImageInfo -> {
            String cosUrl = mjImageInfo.getCosUrl();
            if (!RegexUtils.isHttpOrHttps(cosUrl)) {
                try {
                    cosUrl = OSSFactory.build().getFullUrl(cosUrl);
                    mjImageInfo.setCosUrl(cosUrl);
                } catch (Exception ignored) {

                }
            }
            return mjImageInfo;
        });
        return ResponseResultGenerator.success(convert);
    }

    @Operation(summary = "租户信息查询")
    @GetMapping("/tenantInfo")
    public ResponseResult<TenantInfoRes> tenantInfo() {

        SaveSettingDTO.AiImageSetting aiImageSetting = CommonUtils.getPackageWebConfig().getAiImageSetting();
        if (aiImageSetting == null) {
            return ResponseResultGenerator.success();
        }
        String apiKey = aiImageSetting.getApiKey();
        Integer tenantId = aiImageSetting.getTenantId();
        String res = ApetoMjUtils.tenantInfo(apiKey, tenantId);
        JSONObject jsonObject = JSONUtil.parseObj(res);
        if(!jsonObject.getInt("code").equals(0)){
            return ResponseResultGenerator.success();
        }
        TenantInfoRes tenantInfoRes = jsonObject.getBean("data", TenantInfoRes.class);
        return ResponseResultGenerator.success(tenantInfoRes);
    }
}
