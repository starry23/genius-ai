package cn.apeto.geniusai.server.domain.dto;

import cn.hutool.extra.spring.SpringUtil;
import cn.apeto.geniusai.server.config.properties.AliPayProperties;
import cn.apeto.geniusai.server.config.properties.AliSmsProperties;
import cn.apeto.geniusai.server.config.properties.EmailConfigProperties;
import cn.apeto.geniusai.server.domain.Constants;
import cn.apeto.geniusai.server.domain.MjConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.apeto.geniusai.server.core.mj.ApetoMjServiceImpl.AI_IMAGE_DOMAIN;

/**
 * @author apeto
 * @create 2023/5/5 13:45
 */
@Component
@Schema(description = "SaveSettingDTO")
@Data
public class SaveSettingDTO {

    @Schema(description = "1:邮箱 2:手机号 3:微信授权")
    private List<Integer> loginTypes;
    @Schema(description = "是否打开注册入口 true打开  false关闭")
    private Boolean registerOpen = true;
    @Schema(description = "登录网页显示类型  默认2")
    private Integer loginViewType = 2;
    @Schema(description = "网站名称")
    private String webName;
    @Schema(description = "C端网站名称")
    private String domain = SpringUtil.getProperty("appServer.domain");
    @Schema(description = "二级名称")
    private String subTitle;
    @Schema(description = "icon地址")
    private String iconUrl;
    @Schema(description = "网站版本")
    private String webVersion = Constants.VERSION;
    @Schema(description = "代币名称")
    private String currencyName = "方糖";
    @Schema(description = "备案号")
    private String filingNumber;
    @Schema(description = "网站公告")
    private String announcement;
    @Schema(description = "左侧菜单配置")
    private List<Integer> leftMenuIds;
    @Schema(description = "系统主题")
    private String sysTheme;
    @Schema(description = "首页弹窗")
    private String indexPopup;
    @Schema(description = "搜索APIkey: https://serpapi.com/")
    private String searchApiApiKey;
    @Schema(description = "搜索APIkey开关")
    private Boolean searchApiEnable = false;


    @Schema(description = "活动配置")
    private ActivitySetting activitySetting = new ActivitySetting();

    @Schema(description = "邮箱配置")
    private EmailConfigProperties emailConfigProperties;
    @Schema(description = "百度文本敏感校验setting")
    private BaiduSetting baiduSetting = new BaiduSetting();
    @Schema(description = "AI绘画选择  apeto | bltcy")
    private String aiImageServiceType = MjConstants.AiImageTypeEnum.APETO.getType();
    @Schema(description = "AI绘画配置")
    private AiImageSetting aiImageSetting = new AiImageSetting(AI_IMAGE_DOMAIN, "https://cdn.discord.warape.top");
    @Schema(description = "柏拉图绘画配置")
    private AiImageSetting aiImageBltcySetting = new AiImageSetting("https://one-api.bltcy.top", "https://cdn.discordapp.com");
    @Deprecated
    @Schema(description = "支付宝支付配置")
    private AliPayProperties aliPayProperties;

    @Schema(description = "阿里短信配置")
    private AliSmsProperties aliSmsSetting;
//    @Schema(description = "微信公众号配置")
//    private WxMpProperties wxMpProperties = SpringUtil.getBean(WxMpProperties.class);

    @Data
    public static class CosConfig {
        @Schema(description = "secretId")
        private String secretId;
        @Schema(description = "secretKey")
        private String secretKey;
        @Schema(description = "region")
        private String region;
        @Schema(description = "Bucket")
        private String bucketName;
    }

    @Data
    public static class ActivitySetting {
        @Schema(description = "邀请赠送代币 0代表不赠送")
        private Integer inviteGiveCurrency = 10;
        @Schema(description = "注册赠送代币 0代表不赠送")
        private Integer registerGiveCurrency = 10;
        @Schema(description = "公众号领取知识库上传次数")
        private Integer knowledgeMpGive = 10;
        @Schema(description = "公众号领取代币 0代表不赠送")
        private Integer mpGiveCurrency = 10;
        @Schema(description = "余额为0后弹窗赠送代币 每天一次")
        private Integer alertCurrency = 10;
        @Schema(description = "返佣配置")
        private RebateConfig rebateConfig = new RebateConfig();


        @Data
        public static class RebateConfig {

            @Schema(description = "是否开启返佣")
            private Boolean enable = false;
            @Schema(description = "返佣1级配置")
            private Rebate rebate1;
            @Schema(description = "返佣2级配置")
            private Rebate rebate2;
            @Schema(description = "返佣3级配置")
            private Rebate rebate3;
        }


        @Data
        public static class Rebate {
            @Schema(description = "单量")
            private Integer num;
            @Schema(description = "返佣天数")
            private Integer rate;
        }

    }

    @Data
    public static class BaiduSetting {

        @Schema(description = "百度文本敏感校验是否开启")
        private Boolean textEnable = false;
        @Schema(description = "百度应用ID")
        private String appId;
        @Schema(description = "百度应用Key")
        private String appKey;
        @Schema(description = "百度应用Secret")
        private String secretKey;
    }

    /**
     * 柏拉图绘画
     */
    @Data
    public static class AiImageSetting {

        @Schema(description = "租户ID")
        private Integer tenantId;

        @Schema(description = "域名")
        private String apiHost;

        @Schema(description = "apiKey")
        private String apiKey;

        @Schema(description = "discord cdn 反代")
        private String discordCdn;

        public AiImageSetting(String apiHost, String discordCdn) {
            this.apiHost = apiHost;
            this.discordCdn = discordCdn;
        }

        public AiImageSetting() {
        }
    }

}
