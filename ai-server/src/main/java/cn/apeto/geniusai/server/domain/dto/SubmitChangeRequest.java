package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ImageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/6/12 14:00
 */
@Schema(description = "SubmitChangeRequest")
@EqualsAndHashCode(callSuper = true)
@Data
public class SubmitChangeRequest extends ImageBaseReq {

    @Schema(description = "任务ID", example = "\"1320098173412546\"")
    private String taskId;

    @Schema(description = "UPSCALE(放大); VARIATION(变换); REROLL(重新生成)", required = true,
            allowableValues = "UPSCALE, VARIATION, REROLL", example = "UPSCALE")
    private String action;

    @Schema(description = "序号(1~4), action为UPSCALE,VARIATION时必传", allowableValues = "range[1, 4]", example = "1")
    private Integer index;
}
