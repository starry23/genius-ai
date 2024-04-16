package cn.apeto.geniusai.server.handler;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.Constants.LoginTypeEnum;
import cn.apeto.geniusai.server.domain.MjConstants;
import cn.apeto.geniusai.server.domain.SystemConstants.RedisKeyEnum;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.domain.dto.SubmitChangeDTO;
import cn.apeto.geniusai.server.entity.InviteLog;
import cn.apeto.geniusai.server.entity.MjImageInfo;
import cn.apeto.geniusai.server.entity.ProductConsumeConfig;
import cn.apeto.geniusai.server.entity.UserInfo;
import cn.apeto.geniusai.server.service.*;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.MathUtils;
import cn.apeto.geniusai.server.utils.SmsUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author apeto
 * @create 2023/4/17 14:22
 */
@Slf4j
@Component
public class CommonHandler {

    @Autowired
    private ExchangeCardDetailService exchangeCardDetailService;
    @Autowired
    private MjImageInfoService mjImageInfoService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private InviteLogService inviteLogService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProductConsumeConfigService productConsumeConfigService;


    @Async("chat")
    public void sendCode(String code, String from, String type, LoginTypeEnum loginTypeEnum) {

        String smsCodeKey = RedisKeyEnum.SMS_CODE.getKey(from, type);
        String smsCodeCountLimitKey = RedisKeyEnum.SMS_CODE_COUNT_LIMIT.getKey(from, type);
        try {
            if (loginTypeEnum.getType().equals(LoginTypeEnum.EMAIL.getType())) {
                CommonUtils.sendEmail(from, "验证码", "您好,您的验证码为: " + code);
            } else if (loginTypeEnum.getType().equals(LoginTypeEnum.PHONE.getType())) {
                if (!SmsUtils.sendSms(code, from)) {
                    throw new RuntimeException(StrUtil.format("发送短信验证码失败: {}", from));
                }
            } else {
                throw new RuntimeException("sendType无此类型");
            }

            StringRedisUtils.setForTimeMIN(smsCodeKey, code, 5);
            StringRedisUtils.setForTimeMIN(smsCodeCountLimitKey, code, 1);
            log.info("sendCode==>  类型:{} 验证码:{}", loginTypeEnum.getDesc(), code);
        } catch (Exception e) {
            log.error("验证码发送异常", e);
            StringRedisUtils.delete(smsCodeKey);
            StringRedisUtils.delete(smsCodeCountLimitKey);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void mjConsume(Long userId, String fileId, String prompt, String mjId, String serviceType, ProductConsumeConfig productConsumeConfig) {
        Integer productType = productConsumeConfig.getProductType();
        exchangeCardDetailService.consume(userId, productType, fileId, productConsumeConfig.getConsumeCurrency());

        MjImageInfo mjImageInfo = new MjImageInfo();
        mjImageInfo.setFileId(fileId);
        mjImageInfo.setUserId(userId);
        mjImageInfo.setFileStatus(MjConstants.TaskStatus.SUBMITTED.name());
        mjImageInfo.setMjId(mjId);
        mjImageInfo.setServiceType(serviceType);
        mjImageInfo.setFilePrompt(prompt);
        mjImageInfo.setProductType(productType);
        mjImageInfo.setChangeButtonInfo(JSONUtil.toJsonStr(MapUtil.builder(MjConstants.ActionEnum.IMAGINE, true).build()));
        mjImageInfoService.save(mjImageInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void mjChange(Long userId, String fileId, String prompt, String mjId, String serviceType, SubmitChangeDTO submitChangeDTO, MjImageInfo oldMjImageInfo, ProductConsumeConfig productConsumeConfig) {

        exchangeCardDetailService.consume(userId, productConsumeConfig.getProductType(), fileId, productConsumeConfig.getConsumeCurrency());

        String action = submitChangeDTO.getAction();
        Integer index = submitChangeDTO.getIndex();

        String changeButtonInfo = oldMjImageInfo.getChangeButtonInfo();
        JSONObject jsonObject = JSONUtil.parseObj(changeButtonInfo);
        jsonObject.set(action + index, true);
        oldMjImageInfo.setChangeButtonInfo(jsonObject.toString());
        mjImageInfoService.updateById(oldMjImageInfo);

        MjImageInfo mjImageInfo = new MjImageInfo();
        mjImageInfo.setFileId(fileId);
        mjImageInfo.setUserId(userId);
        mjImageInfo.setFileStatus(MjConstants.TaskStatus.SUBMITTED.name());
        mjImageInfo.setMjId(mjId);
        mjImageInfo.setFileAction(action);
        mjImageInfo.setProductType(oldMjImageInfo.getProductType());
        mjImageInfo.setServiceType(serviceType);
        mjImageInfo.setFilePrompt(prompt);
        if (MjConstants.ActionEnum.VARIATION.name().equals(action)) {
            mjImageInfo.setChangeButtonInfo(JSONUtil.toJsonStr(MapUtil.builder(MjConstants.ActionEnum.IMAGINE, true).build()));
        }
        mjImageInfoService.save(mjImageInfo);
    }

    /**
     * 计算返佣
     *
     * @param paySn
     * @param userId
     * @param payAmount
     */
    @Async("chat")
    public void rebate(String paySn, Long userId, BigDecimal payAmount) {
        // 计算返佣
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        SaveSettingDTO.ActivitySetting activitySetting = packageWebConfig.getActivitySetting();
        SaveSettingDTO.ActivitySetting.RebateConfig rebateConfig = activitySetting.getRebateConfig();
        if (rebateConfig.getEnable()) {
            //返利
            InviteLog byToInviteUserId = inviteLogService.getByToInviteUserId(userId);
            if (byToInviteUserId == null) {
                return;
            }
            Long inviteUserId = byToInviteUserId.getInviteUserId();
            UserInfo userInfo = userInfoService.getById(inviteUserId);
            Integer inviteCount = userInfo.getInviteCount();
            Integer accountType = Constants.AccountTypeEnum.RMB_BALANCE.getType();
            if (inviteCount != 0) {
                SaveSettingDTO.ActivitySetting.Rebate rebate1 = rebateConfig.getRebate1();
                SaveSettingDTO.ActivitySetting.Rebate rebate2 = rebateConfig.getRebate2();
                SaveSettingDTO.ActivitySetting.Rebate rebate3 = rebateConfig.getRebate3();

                if (rebate1 == null || rebate2 == null || rebate3 == null) {
                    return;
                }

                if (inviteCount <= rebate1.getNum()) {
                    // 一级
                    doRebate(paySn, inviteUserId, accountType, payAmount, inviteCount, rebate1);
                }
                if (inviteCount > rebate1.getNum() && inviteCount <= rebate2.getNum()) {
                    // 二级
                    doRebate(paySn, inviteUserId, accountType, payAmount, inviteCount, rebate2);
                }
                if (inviteCount > rebate2.getNum()) {
                    doRebate(paySn, inviteUserId, accountType, payAmount, inviteCount, rebate3);
                }
            }
        }
    }


    private void doRebate(String paySn, Long userId, Integer accountType, BigDecimal payAmount, long inviteCount, SaveSettingDTO.ActivitySetting.Rebate rebate) {
        if (rebate == null) {
            return;
        }
        Integer num = rebate.getNum();
        Integer rate = rebate.getRate();
        if (num == null || num == 0) {
            return;
        }
        if (rate == null || rate == 0) {
            return;
        }
        if (inviteCount == 0) {
            return;
        }
        if (inviteCount <= num) {
            BigDecimal rebateAmount = MathUtils.rateHalfUp2(payAmount, rate);
            accountService.commonUpdateAccount(userId, accountType, rebateAmount, paySn,
                    rate + "_" + IdUtil.fastSimpleUUID(), Constants.LogDescriptionTypeEnum.REBATE,
                    Constants.DirectionTypeEnum.IN, "system");
        }
    }
}
