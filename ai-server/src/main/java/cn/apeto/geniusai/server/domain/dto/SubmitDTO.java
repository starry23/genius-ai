package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author apeto
 * @create 2023/6/9 12:21
 */
@Data
@Schema(description = "SubmitDTO")
public class SubmitDTO {

    @Schema(description = "提示词", example = "Cat")
    private String prompt;

    @Schema(description = "垫图base64数组")
    private List<String> base64Array;

    private Integer productType;

}
