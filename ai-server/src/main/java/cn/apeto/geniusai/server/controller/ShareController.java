package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.KnowledgeConstants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.vo.ShareInfoVO;
import cn.apeto.geniusai.server.domain.vo.ShareSettingVO;
import cn.apeto.geniusai.server.entity.ShareItem;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.service.KnowledgeItemService;
import cn.apeto.geniusai.server.service.ShareItemService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * @author apeto
 * @create 2023/8/3 17:17
 */
@Slf4j
@RestController
@RequestMapping("/api/share")
@Tag(name = "分享")
public class ShareController {


    @Autowired
    private ShareItemService shareItemService;

    @Autowired
    private KnowledgeItemService knowledgeItemService;

    @Operation(summary = "分享设置")
    @PostMapping("/setting")
    public ResponseResult<?> setting(@RequestBody ShareSettingVO shareSettingVO) {

        long userId = StpUtil.getLoginIdAsLong();
        ShareItem shareItem = shareItemService.getByUidAndItemId(userId, shareSettingVO.getItemId());
        shareItem.setIsEnable(shareSettingVO.getIsEnable());
        shareItemService.updateById(shareItem);
        return ResponseResultGenerator.success();
    }

    @Operation(summary = "分享信息")
    @GetMapping("/info")
    public ResponseResult<ShareInfoVO> shareInfo(Long itemId, Integer shareType) {

        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        if (Objects.isNull(itemId)) {
            return ResponseResultGenerator.result(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
        }
        long userId = StpUtil.getLoginIdAsLong();
        // 判断item是否存在
        if (Objects.isNull(knowledgeItemService.getById(itemId))) {
            return ResponseResultGenerator.result(CommonRespCode.VALID_REQUEST_PARAM);
        }

        ShareItem shareItem = shareItemService.getByUidAndItemId(userId, itemId);

        String uuid;
        if (Objects.isNull(shareItem)) {
            shareItem = new ShareItem();
            shareItem.setUserId(userId);
            shareItem.setItemId(itemId);
            uuid = IdUtil.fastSimpleUUID();
            shareItem.setUuid(uuid);
            shareItem.setIsEnable(KnowledgeConstants.ShareStatusEnum.NOT.getCode());
            if (!shareItemService.save(shareItem)) {
                log.error("插入分享链接失败,userId：{}，itemId：{}", userId, itemId);
                throw new ServiceException(500, "插入分享链接失败");
            }
        } else {
            uuid = shareItem.getUuid();
        }
        ShareInfoVO vo = new ShareInfoVO();
        vo.setDomain(packageWebConfig.getDomain());
        vo.setKey(uuid);
        vo.setIsEnable(shareItem.getIsEnable());
        vo.setShareType(shareItem.getShareType());
        return ResponseResultGenerator.success(vo);
    }

}
