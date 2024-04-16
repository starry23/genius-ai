package cn.apeto.geniusai.server.domain.vo;

import cn.apeto.geniusai.server.domain.KnowledgeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/8/3 17:22
 */
@Schema(description = "ShareInfoVO")
@Data
public class ShareInfoVO {

    @Schema(description = "域名")
    private String domain;

    @Schema(description = "key")
    private String key;

    @Schema(description = "类型 10: 知识库web分享",defaultValue = "10")
    private Integer shareType = KnowledgeConstants.ShareTypeEnum.WEB.getCode();

    @Schema(description = "是否分享  0 不分享 1: 分享")
    private Integer isEnable;
}
