package cn.apeto.geniusai.server.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.ResponseResult;
import cn.apeto.geniusai.server.domain.ResponseResultGenerator;
import cn.apeto.geniusai.server.domain.SystemConstants;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import cn.apeto.geniusai.server.entity.Account;
import cn.apeto.geniusai.server.service.AccountService;
import cn.apeto.geniusai.server.utils.CommonUtils;
import cn.apeto.geniusai.server.utils.StringRedisUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author apeto
 * @create 2023/4/5 5:18 下午
 */
@Tag(name = "活动相关")
@RequestMapping("/api/activity")
@RestController
public class ActivityController {

    @Value("${activity.invite-config.url}")
    private String url;
    @Autowired
    private AccountService accountService;

    @Operation(summary = "获取邀请有礼链接信息")
    @GetMapping("/inviteGiftUrlInfo")
    public ResponseResult<Map<String, String>> inviteGiftUrlInfo() {

        Map<String, String> map = new HashMap<>();
        String desc = "下面是您的专属链接";
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        String currencyName = packageWebConfig.getCurrencyName();
        Integer inviteGiveCurrency = packageWebConfig.getActivitySetting().getInviteGiveCurrency();
        if (inviteGiveCurrency != null && inviteGiveCurrency > 0) {
            String str = "当您通过分享您的专属二维码或链接邀请好友成功注册后，将永久获得{}个{}。快来邀请您的好友加入我们吧！";
            desc = StrUtil.format(str, inviteGiveCurrency, currencyName);
        }
        long userId = StpUtil.getLoginIdAsLong();
        String encryptHex = CommonUtils.inviteSymmetricCrypto().encryptHex(String.valueOf(userId));
        map.put("url", url);
        map.put("code", encryptHex);
        map.put("desc", desc);
        return ResponseResultGenerator.success(map);
    }

    @Operation(summary = "弹窗赠送代币")
    @PostMapping("/alertGiveCurrency")
    public ResponseResult<Integer> alertGiveCurrency() {
        DateTime date = DateUtil.date();
        long userId = StpUtil.getLoginIdAsLong();
        String formatDate = DateUtil.formatDate(date);
        Account account = accountService.getByUserIdAndType(userId, Constants.AccountTypeEnum.TOKEN_BALANCE.getType());
        String key = SystemConstants.RedisKeyEnum.ALERT_GIVE_CURRENCY.getKey(userId, formatDate);
        String value = StringRedisUtils.get(key);
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        SaveSettingDTO.ActivitySetting activitySetting = packageWebConfig.getActivitySetting();
        Integer alertCurrency = activitySetting.getAlertCurrency();
        if (account.getAccountBalance().compareTo(BigDecimal.ZERO) > 0 || alertCurrency <= 0 || StrUtil.isNotBlank(value)) {
            return ResponseResultGenerator.result(Constants.ResponseEnum.ACTIVITY_END);
        }
        StringRedisUtils.setForTimeCustom(key, "1", 1L, TimeUnit.DAYS);
        String reqId = IdUtil.fastSimpleUUID();
        Constants.LogDescriptionTypeEnum recharge = Constants.LogDescriptionTypeEnum.ACTIVITY_GIVE;
        Constants.DirectionTypeEnum directionTypeEnum = Constants.DirectionTypeEnum.IN;
        accountService.commonUpdateAccount(userId, BigDecimal.valueOf(alertCurrency), reqId, reqId, recharge, directionTypeEnum, "余额不足赠送活动");
        return ResponseResultGenerator.success(alertCurrency);
    }

}
