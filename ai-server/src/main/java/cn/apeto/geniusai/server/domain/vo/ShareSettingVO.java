package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/8/6 4:52 下午
 */
@Data
public class ShareSettingVO {

    @Schema(description = "项目ID")
    private Long ItemId;

    @Schema(description = "分享类型 10: web分享")
    private Integer shareType;

    @Schema(description = "是否分享  0 不分享 1: 分享")
    private Integer isEnable;
}
