package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wanmingyu
 * @create 2023/11/14 17:25
 */
@Data
@Schema(defaultValue = "openai生成图片")
public class DallGenerateDTO {

    @Schema(defaultValue = "提示词")
    private String prompt;
    @Schema(defaultValue = "大小", example = "1024x1024, 1792x1024, 1024x1792")
    private String size;
}
