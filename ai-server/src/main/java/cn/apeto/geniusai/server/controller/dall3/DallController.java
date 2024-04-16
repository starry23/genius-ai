package cn.apeto.geniusai.server.controller.dall3;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.apeto.geniusai.sdk.OpenAiStreamClient;
import cn.apeto.geniusai.sdk.entity.chat.ChatCompletion;
import cn.apeto.geniusai.sdk.entity.images.Image;
import cn.apeto.geniusai.sdk.entity.images.ImageResponse;
import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.DallGenerateDTO;
import cn.apeto.geniusai.server.domain.vo.GenerateRecordVO;
import cn.apeto.geniusai.server.entity.DallEImageInfo;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.service.DallEImageInfoService;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.service.ProductConsumeConfigService;
import cn.apeto.geniusai.server.service.oss.OSSFactory;
import cn.apeto.geniusai.server.service.oss.UploadResult;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wanmingyu
 * @create 2023/11/14 17:16
 */
@Slf4j
@Tag(name = "DALL3绘画")
@RestController
@RequestMapping("/api/openai-dall-e")
@Validated
public class DallController {

    @Autowired
    private OpenAiStreamClient openAiStreamClient;
    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;
    @Autowired
    private DallEImageInfoService dallEImageInfoService;

    @Schema(description = "DALL_E生成图片")
    @PostMapping("/generate")
    @DistributedLock(prefix = SystemConstants.RedisKeyEnum.DALLE_GENERATE,waitFor = 0, expire = 60,isReqUserId = true)
    public ResponseResult<String> generate(@RequestBody DallGenerateDTO dallGenerateDTO) {

        Integer productType = Constants.ProductTypeEnum.DALL_E.getType();

        ProductConsumeConfig productConsumeConfig = productConsumeConfigService.getByType(productType);
        Long consumeCurrency = productConsumeConfig.getConsumeCurrency();

        String prompt = dallGenerateDTO.getPrompt();
        long userId = StpUtil.getLoginIdAsLong();
        String uuid = IdUtil.fastSimpleUUID();

        exchangeCardDetailService.checkConsume(userId, productType, consumeCurrency);
        Image image = Image.builder()
                .responseFormat(cn.apeto.geniusai.sdk.entity.images.ResponseFormat.URL.getName())
                .model(Image.Model.DALL_E_3.getName())
                .prompt(prompt)
                .n(1)
                .modelType(ChatCompletion.ModelType.DALL_E)
                .quality(Image.Quality.HD.getName())
                .size(dallGenerateDTO.getSize())
                .style(Image.Style.VIVID.getName())
                .build();
        ImageResponse imageResponse = openAiStreamClient.genImages(image);
        String url = imageResponse.getData().get(0).getUrl();
        if (StrUtil.isBlank(url)) {
            return ResponseResultGenerator.error("生成失败");
        }

        String cosUrl = "";
        String fullCurl = "";
        try {
            UploadResult uploadResult = OSSFactory.build().uploadUrl(url);
            fullCurl = uploadResult.getFullUrl();
            cosUrl = uploadResult.getPath();
        } catch (Exception e) {
            log.error("上传对象存储异常", e);
        }

        DallEImageInfo dallEImageInfo = new DallEImageInfo();
        dallEImageInfo.setUserId(userId);
        dallEImageInfo.setFileId(uuid);
        dallEImageInfo.setOpenaiImageUrl(url);
        dallEImageInfo.setCosUrl(cosUrl);
        dallEImageInfo.setFilePrompt(prompt);
        dallEImageInfoService.save(dallEImageInfo);
        exchangeCardDetailService.consume(userId, productType, uuid, consumeCurrency);

        return ResponseResultGenerator.success(fullCurl);
    }

    @Schema(description = "获取生成记录")
    @GetMapping("/getGenerateRecord")
    public ResponseResult<List<GenerateRecordVO>> getGenerateRecord() {

        long userId = StpUtil.getLoginIdAsLong();
        List<DallEImageInfo> dallEImageInfoList = dallEImageInfoService.selectByUserId(userId);

        List<GenerateRecordVO> result = dallEImageInfoList.stream().map(dallEImageInfo -> {
            GenerateRecordVO vo = new GenerateRecordVO();
            vo.setUrl(OSSFactory.build().getFullUrl(dallEImageInfo.getCosUrl()));
            vo.setPrompt(dallEImageInfo.getFilePrompt());
            vo.setFileId(dallEImageInfo.getFileId());
            return vo;
        }).collect(Collectors.toList());
        return ResponseResultGenerator.success(result);
    }

    @Schema(description = "删除")
    @DeleteMapping("/remove")
    public ResponseResult<String> remove(@RequestParam("fileId") String fileId) {
        dallEImageInfoService.removeByFileId(fileId);
        return ResponseResultGenerator.success();
    }
}
