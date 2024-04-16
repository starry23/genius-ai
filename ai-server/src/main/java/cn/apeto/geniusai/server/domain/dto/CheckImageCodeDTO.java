package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Schema(description = "CheckImageCodeDTO")
@Data
public class CheckImageCodeDTO {

    @NotBlank
    @Schema(description = "账户")
    private String accountNum;
    @NotBlank
    @Schema(description = "验证码")
    private String code;

}
