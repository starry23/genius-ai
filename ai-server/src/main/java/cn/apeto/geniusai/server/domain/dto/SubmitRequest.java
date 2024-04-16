package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ImageBaseReq;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/5/26 10:57 下午
 */
@Data
@Schema(description = "SubmitRequest")
@EqualsAndHashCode(callSuper = true)
public class SubmitRequest extends ImageBaseReq {

  @Schema(description = "提示词", example = "Cat")
  private String prompt;

  @Schema(description = "垫图base64", example = "data:image/png;base64,xxx")
  private String base64;


}
