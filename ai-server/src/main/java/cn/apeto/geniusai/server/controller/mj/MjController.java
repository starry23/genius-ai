package cn.apeto.geniusai.server.controller.mj;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.core.mj.MJService;
import cn.apeto.geniusai.server.domain.SubmitRequest;
import cn.apeto.geniusai.server.domain.*;
import cn.apeto.geniusai.server.domain.dto.*;
import cn.apeto.geniusai.server.domain.vo.MjImageInfoVO;
import cn.apeto.geniusai.server.entity.BaseEntity;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.handler.CommonHandler;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.MjImageInfoService;
import cn.apeto.geniusai.server.service.ProductConsumeConfigService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.utils.BaiduUtils;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.RegexUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/5/26 10:56 下午
 */
@Slf4j
@Tag(name = "mj绘画")
@RestController
@RequestMapping("/api/mj")
@Validated
public class MjController {

    @Value("${appServer.domain}")
    private String domain;
    @Autowired
    private MjImageInfoService mjImageInfoService;
    @Autowired
    private CommonHandler commonHandler;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;


    @SaIgnore
    @Operation(summary = "回调")
    @PostMapping("/hook")
    public ResponseResult<?> hook(@RequestBody MjHookInfo mjHookInfo) {
        log.info("hook:{}", JSONUtil.toJsonStr(mjHookInfo));
        mjImageInfoService.saveHook(mjHookInfo);
        return ResponseResultGenerator.success();
    }

    private String getMode(Integer productType) {
        return MjConstants.ModeEnum.getByProductType(productType);
    }

    @Operation(summary = "生成图片")
    @PostMapping("/imagine")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.MJ_GENERATE, waitFor = 0, expire = 120, isReqUserId = true)
    public ResponseResult<?> submit(@RequestBody SubmitDTO submitDTO) {
        String fileId = IdUtil.fastSimpleUUID();
        long userId = StpUtil.getLoginIdAsLong();
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        String aiImageServiceType = packageWebConfig.getAiImageServiceType();
        StateDTO stateDTO = getStateDTO(fileId, userId);
        String prompt = submitDTO.getPrompt();
        Integer productType = submitDTO.getProductType();

        if (productType == null || (!Constants.ProductTypeEnum.MJ.getType().equals(productType) && !Constants.ProductTypeEnum.MJ_FAST.getType().equals(productType))) {
            return ResponseResultGenerator.result(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }

        SaveSettingDTO.BaiduSetting baiduSetting = packageWebConfig.getBaiduSetting();
        if (baiduSetting.getTextEnable() && !BaiduUtils.baiduTextCensor(prompt)) {
            return ResponseResultGenerator.result(CommonRespCode.SENSITIVE);
        }

        ProductConsumeConfig productConsumeConfig = productConsumeConfigService.getByType(productType);

        exchangeCardDetailService.checkConsume(userId, productType, productConsumeConfig.getConsumeCurrency());

        SubmitRequest request = new SubmitRequest();
        request.setPrompt(prompt);
        request.setMode(getMode(productType));
        request.setState(JSONUtil.toJsonStr(stateDTO));
        request.setNotifyHook(getNotifyHook());
        request.setBase64Array(submitDTO.getBase64Array());
        ImageResponse imageResponse = MJService.MJ_MAP.get(aiImageServiceType).imagine(request);
        String result = imageResponse.getResult();
        commonHandler.mjConsume(userId, fileId, prompt, result, aiImageServiceType, productConsumeConfig);
        return ResponseResultGenerator.success(fileId);
    }


    private StateDTO getStateDTO(String fileId, long userId) {
        StateDTO stateDTO = new StateDTO();
        stateDTO.setUserId(userId);
        stateDTO.setFileId(fileId);
        return stateDTO;
    }

    private String getNotifyHook() {
        return domain + "/api/mj/hook";
    }

    @Operation(summary = "绘图变化")
    @PostMapping("/change")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.QUESTIONS_LOCK, key = "#submitChangeDTO.getFileId()", waitFor = 0, isReqUserId = true)
    public ResponseResult<?> change(@RequestBody SubmitChangeDTO submitChangeDTO) {
        String fileId = submitChangeDTO.getFileId();
        MjImageInfo mjImageInfo = mjImageInfoService.getByFileId(fileId);
        if (DateUtil.betweenDay(mjImageInfo.getCreateTime(), new Date(), false) > 30) {
            return ResponseResultGenerator.result(MjConstants.ResponseEnum.EXISTED);
        }
        Integer productType = mjImageInfo.getProductType();
        String action = submitChangeDTO.getAction();
        Integer index = submitChangeDTO.getIndex();
        String serviceType = mjImageInfo.getServiceType();
        String mjId = mjImageInfo.getMjId();
        String filePrompt = mjImageInfo.getFilePrompt();
        long userId = StpUtil.getLoginIdAsLong();

        ProductConsumeConfig productConsumeConfig = productConsumeConfigService.getByType(productType);
        exchangeCardDetailService.checkConsume(userId, productType, productConsumeConfig.getConsumeCurrency());

        String newFileId = IdUtil.fastSimpleUUID();
        StateDTO stateDTO = getStateDTO(newFileId, userId);

        MJService mjService = MJService.MJ_MAP.get(serviceType);
        SubmitChangeRequest request = new SubmitChangeRequest();
        request.setAction(action);
        request.setIndex(index);
        request.setMode(getMode(productType));
        request.setTaskId(mjId);
        request.setNotifyHook(getNotifyHook());
        request.setState(JSONUtil.toJsonStr(stateDTO));
        ImageResponse imageResponse = mjService.change(request);
        String result = imageResponse.getResult();
        commonHandler.mjChange(userId, newFileId, filePrompt, result, serviceType, submitChangeDTO, mjImageInfo, productConsumeConfig);
        return ResponseResultGenerator.success(newFileId);
    }


    @Operation(summary = "获取图片")
    @GetMapping("/getImage")
    public ResponseResult<MjImageInfoVO> getImage(@RequestParam("fileId") String fileId) {

        long userId = StpUtil.getLoginIdAsLong();
        MjImageInfo mjImageInfo = mjImageInfoService.getByFileId(fileId);
        if (!mjImageInfo.getUserId().equals(userId)) {
            return ResponseResultGenerator.result(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }

        return ResponseResultGenerator.success(convertMjImageInfoVO(mjImageInfo));
    }

    @Operation(summary = "终止")
    @GetMapping("/stop")
    public ResponseResult<MjImageInfoVO> stop(@RequestParam("fileId") String fileId) {

        StpUtil.getLoginIdAsLong();
        MjImageInfo mjImageInfo = mjImageInfoService.getByFileId(fileId);
        mjImageInfo.setFileStatus(MjConstants.TaskStatus.FAILURE.name());
        mjImageInfoService.updateById(mjImageInfo);
        return ResponseResultGenerator.success(convertMjImageInfoVO(mjImageInfo));
    }

    @Operation(summary = "删除图片")
    @DeleteMapping("/remove/{fileId}")
    public ResponseResult<?> remove(@PathVariable("fileId") String fileId) {
        StpUtil.checkLogin();
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getFileId, fileId);
        mjImageInfoService.remove(queryWrapper);
        return ResponseResultGenerator.success();
    }


    private MjImageInfoVO convertMjImageInfoVO(MjImageInfo mjImageInfo) {
        MjImageInfoVO vo = new MjImageInfoVO();
        String cosUrl = mjImageInfo.getCosUrl();
        if (!RegexUtils.isHttpOrHttps(cosUrl)) {
            try {
                cosUrl = OSSFactory.build().getFullUrl(cosUrl);
            } catch (Exception ignored) {

            }
        }
        vo.setCosUrl(cosUrl);
        vo.setFileAction(mjImageInfo.getFileAction());
        vo.setFileStatus(mjImageInfo.getFileStatus());
        vo.setFilePrompt(mjImageInfo.getFilePrompt());
        vo.setProgress(mjImageInfo.getProgress());
        vo.setFileId(mjImageInfo.getFileId());
        vo.setCreateTime(mjImageInfo.getCreateTime());
        vo.setPublishState(mjImageInfo.getPublishState());
        String changeButtonInfo = mjImageInfo.getChangeButtonInfo();
        if (StrUtil.isNotBlank(changeButtonInfo)) {
            vo.setChangeButtonInfo(JSONUtil.parseObj(changeButtonInfo));
        }
        return vo;
    }

    @Operation(summary = "图片列表分页")
    @PostMapping("/images")
    public ResponseResult<IPage<MjImageInfoVO>> images(@RequestBody ReqPage reqPage) {
        Long userId = StpUtil.getLoginIdAsLong();
        Page<MjImageInfo> page = new Page<>(reqPage.getCurrent(), reqPage.getSize());
        Page<MjImageInfo> mjImageInfos = mjImageInfoService.selectByUserIdPage(page, userId);
        IPage<MjImageInfoVO> result = mjImageInfos.convert(this::convertMjImageInfoVO);
        return ResponseResultGenerator.success(result);
    }


    @SaIgnore
    @Operation(summary = "风格广场分页")
    @PostMapping("/imagesSquare")
    public ResponseResult<IPage<MjImageInfoVO>> imagesSquare(@RequestBody ReqPage reqPage) {
        Page<MjImageInfo> page = new Page<>(reqPage.getCurrent(), reqPage.getSize());
        LambdaQueryWrapper<MjImageInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MjImageInfo::getPublishState, MjConstants.PublishStateEnum.PUBLISHED.name());
        queryWrapper.orderByDesc(BaseEntity::getId);
        Page<MjImageInfo> mjImageInfos = mjImageInfoService.page(page, queryWrapper);
        IPage<MjImageInfoVO> result = mjImageInfos.convert(this::convertMjImageInfoVO);
        return ResponseResultGenerator.success(result);
    }


    @Operation(summary = "发布")
    @PostMapping("/publishState")
    public ResponseResult<?> publishState(@RequestBody PublishStateDTO publishState) {
        StpUtil.checkLogin();
        MjImageInfo mjImageInfo = mjImageInfoService.getByFileId(publishState.getFileId());
        mjImageInfo.setPublishState(publishState.getPublishState());
        mjImageInfoService.updateById(mjImageInfo);
        return ResponseResultGenerator.success();
    }

}
