package cn.apeto.geniusai.server.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alipay.api.AlipayClient;
import com.ijpay.alipay.AliPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfig;
import com.ijpay.wxpay.WxPayApiConfigKit;
import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.domain.PaySetting;
import cn.apeto.geniusai.server.domain.WechatPayConfig;
import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author apeto
 * @create 2023/6/3 8:02 下午
 */
@Slf4j
@Data
public class PayUtils {


    public static AlipayClient alipayClient() {
        PaySetting paySetting = CommonUtils.getPaySetting();
        AliPayProperties aliPayProperties = paySetting.getAliPayProperties();
        if (aliPayProperties == null) {
            aliPayProperties = SpringUtil.getBean(AliPayProperties.class);
        }
        if (aliPayProperties == null || StrUtil.isBlank(aliPayProperties.getAppId())) {
            return null;
        }
        return AliPayApiConfig.builder()
                .setAppId(aliPayProperties.getAppId())
                .setAliPayPublicKey(aliPayProperties.getAliPublicKey())
                .setCharset("UTF-8")
                .setPrivateKey(aliPayProperties.getAppPrivateKey())
                .setServiceUrl(null)
                .setSignType("RSA2")
                .build().getAliPayClient();
    }

    public static WxPayApiConfig wxPayApiConfig() {
        SaveSettingDTO packageWebConfig = CommonUtils.getPackageWebConfig();
        PaySetting paySetting = CommonUtils.getPaySetting();
        WechatPayConfig wechatPayConfig = paySetting.getWechatPayConfig();
        WxPayApiConfig apiConfig;

        try {
            apiConfig = WxPayApiConfigKit.getApiConfig(wechatPayConfig.getAppId());
        } catch (Exception e) {
            apiConfig = WxPayApiConfig.builder()
                    .appId(wechatPayConfig.getAppId())
                    .mchId(wechatPayConfig.getMchId())
                    .partnerKey(wechatPayConfig.getPartnerKey())
                    .domain(packageWebConfig.getDomain())
                    .build();
        }
        return apiConfig;
    }
}
