package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wanmingyu
 * @create 2024/1/25 12:56 下午
 */
@Schema(description = "绑定机器人")
@Data
public class BindingBotVO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "appid")
    private String appId;

    @Schema(description = "aesKey")
    private String aesKey;

    @Schema(description = "token")
    private String token;

    @Schema(description = "机器人名称")
    private String botName;

    @Schema(description = "客服名称")
    private String kefuName;

    @Schema(description = "机器人描述")
    private String botDesc;

    @Schema(description = "产品类型")
    private Integer productType;
    @Schema(description = "知识库ID")
    private Long knowledgeId;

    @Schema(description = "状态 0下线 1上线 默认为0")
    private Integer state = 0;

    @Schema(description = "h5URL")
    private String h5Url;

    @Schema(description = "客服头像", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kefuAvatar;
    @Schema(description = "客服头像", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kefuAvatarView;
}
