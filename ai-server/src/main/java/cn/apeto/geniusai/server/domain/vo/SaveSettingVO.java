package cn.apeto.geniusai.server.domain.vo;

import cn.apeto.geniusai.server.domain.dto.SaveSettingDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author apeto
 * @create 2023/6/2 12:15
 */
@Schema(description = "SaveSettingVO")
@Data
public class SaveSettingVO {

    @Schema(description = "1:邮箱 2:手机号 3:微信授权")
    private List<Integer> loginTypes;
    private Boolean registerOpen;
    @Schema(description = "登录网页显示类型  默认2")
    private Integer loginViewType = 2;
    @Schema(description = "网站名称")
    private String webName;
    @Schema(description = "二级名称")
    private String subTitle;
    @Schema(description = "网站版本")
    private String webVersion;
    @Schema(description = "icon地址")
    private String iconUrl;
    @Schema(description = "备案号")
    private String filingNumber;
    @Schema(description = "网站公告")
    private String announcement;
    @Schema(description = "代币名称")
    private String currencyName;
    @Schema(description = "左侧菜单配置")
    private List<Integer> leftMenuIds;
    @Schema(description = "系统主题")
    private String sysTheme;
    @Schema(description = "活动配置")
    private SaveSettingDTO.ActivitySetting activitySetting;

    @Schema(description = "支付宝支付是否启用")
    private Boolean aliPayEnable;
    @Schema(description = "是否强制开启当面付")
    private Boolean openFacePay;
    @Schema(description = "微信支付是否启用")
    private Boolean wechatPayEnable;

    @Schema(description = "关闭支付公告")
    private String closePayNotice;

    @Schema(description = "首页弹窗")
    private String indexPopup;

    @Schema(description = "搜索APIkey开关")
    private Boolean searchApiEnable = false;

}
