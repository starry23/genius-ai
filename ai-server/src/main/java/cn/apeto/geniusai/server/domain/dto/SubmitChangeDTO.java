package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


/**
 * @author apeto
 * @create 2023/6/12 14:00
 */
@Schema(description = "SubmitChangeDTO")
@Data
public class SubmitChangeDTO {

    @NotBlank
    @Schema(description = "fileId", example = "fileId")
    private String fileId;

    @NotBlank
    @Schema(description = "UPSCALE(放大); VARIATION(变换); REROLL(重新生成)", required = true,
            allowableValues = "UPSCALE, VARIATION, REROLL", example = "UPSCALE")
    private String action;

    @Schema(description = "序号(1~4), action为UPSCALE,VARIATION时必传", allowableValues = "range[1, 4]", example = "1")
    private Integer index;

}
