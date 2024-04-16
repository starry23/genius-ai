package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.sdk.exception.BaseException;
import cn.apeto.geniusai.server.annotations.DistributedLock;
import cn.apeto.geniusai.server.domain.ChatInfo;
import cn.apeto.geniusai.server.domain.CommonRespCode;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.exception.ServiceException;
import cn.apeto.geniusai.server.exception.SseEmitterException;
import cn.apeto.geniusai.server.service.ExchangeCardDetailService;
import cn.apeto.geniusai.server.utils.BaiduUtils;
import cn.apeto.geniusai.server.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static cn.apeto.geniusai.server.domain.Constants.ResponseEnum.NOT_LOGIN;

/**
 * @author apeto
 * @create 2023/3/27 11:23
 */
@Slf4j
@RestController
@Tag(name = "chat相关")
@RequestMapping("/api/chat")
public class ChatQuestionsController {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;

    @CrossOrigin
    @Operation(summary = "问答")
    @GetMapping("/questions")
    @DistributedLock(prefix = RedisKeyEnum.QUESTIONS_LOCK, key = "#chatInfo.getReqId()", waitFor = 0, isReqUserId = true)
    public SseEmitter questions(@Validated ChatInfo chatInfo) throws IOException {

        String prompt = chatInfo.getPrompt();
        Integer productType = chatInfo.getProductType();
        SseEmitter sseEmitter = new SseEmitter(0L);

        try {

            Object loginIdDefaultNull = StpUtil.getLoginIdDefaultNull();
            if (loginIdDefaultNull == null) {
                throw new SseEmitterException(NOT_LOGIN);
            }

            if (StrUtil.isBlank(prompt)) {
                throw new SseEmitterException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
            }


            Constants.ProductTypeEnum productTypeEnum = Constants.ProductTypeEnum.getByEnum(productType);
            if (productTypeEnum == null) {
                throw new SseEmitterException(CommonRespCode.VALID_SERVICE_ILLEGAL_ARGUMENT);
            }

            Long userId = Long.valueOf(loginIdDefaultNull.toString());

            // 检查账户
            exchangeCardDetailService.checkConsume(userId, productType, (long) prompt.length());

            SaveSettingDTO settingDTO = CommonUtils.getPackageWebConfig();
            SaveSettingDTO.BaiduSetting baiduSetting = settingDTO.getBaiduSetting();
            if (baiduSetting.getTextEnable() && !BaiduUtils.baiduTextCensor(prompt)) {
                sseEmitter.send(SseEmitter.event()
                        .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(CommonRespCode.SENSITIVE)))
                        .reconnectTime(3000));
                return sseEmitter;
            }
            // 填充
            chatInfo.setUserId(userId);
            // 问答
            SpringUtil.getBean(productTypeEnum.getChatClass()).doChat(sseEmitter, chatInfo);

        } catch (SseEmitterException e) {
            log.error("questions SseEmitterException:{}", e.getMessage());
            sseEmitter.send(SseEmitter.event()
                    .data(e.toString())
                    .reconnectTime(3000));
            sseEmitter.complete();
        } catch (BaseException e) {
            log.error("questions BaseException:{}", e.getMessage());
            sseEmitter.send(SseEmitter.event()
                    .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(e.getCode(), e.getMessage())))
                    .reconnectTime(3000));
            sseEmitter.complete();
        } catch (ServiceException e) {
            log.error("questions ServiceException:{}", e.getMessage());
            sseEmitter.send(SseEmitter.event()
                    .data(JSONUtil.toJsonStr(ResponseResultGenerator.result(e.getCode(), e.getMessage())))
                    .reconnectTime(3000));
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("questions Exception", e);
            sseEmitter.send(SseEmitter.event()
                    .data(CommonRespCode.ERROR.toJsonStr())
                    .reconnectTime(3000));
            sseEmitter.complete();
        }

        return sseEmitter;
    }

}
