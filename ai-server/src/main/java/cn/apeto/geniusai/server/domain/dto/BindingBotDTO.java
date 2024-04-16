package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author wanmingyu
 * @create 2024/1/25 12:56 下午
 */
@Schema(description = "绑定机器人", requiredMode = Schema.RequiredMode.REQUIRED)
@Data
public class BindingBotDTO {

    @Schema(description = "id", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long id;

    @NotBlank
    @Schema(description = "appid")
    private String appId;

    @NotBlank
    @Schema(description = "aesKey")
    private String aesKey;

    @NotBlank
    @Schema(description = "token")
    private String token;

    @NotBlank
    @Schema(description = "机器人名称")
    private String botName;

    @Schema(description = "知识库reqId")
    private Long knowledgeId;

    @NotBlank
    @Schema(description = "客服名称")
    private String kefuName;

    @Schema(description = "客服头像", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String kefuAvatar;

    @Schema(description = "机器人描述", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String botDesc;

    @NotNull
    @Schema(description = "产品类型")
    private Integer productType;

    @NotNull
    @Schema(description = "状态 0下线 1上线 默认为0")
    private Integer state = 0;
}
