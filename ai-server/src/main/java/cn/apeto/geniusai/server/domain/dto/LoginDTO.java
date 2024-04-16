package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author apeto
 * @create 2023/4/11 21:25
 */
@Data
public class LoginDTO {

  @Schema(description = "账号")
  @NotBlank
  private String accountNum;

  @Schema(description = "类型 1: 邮箱 2: 手机号")
  @NotNull
  private Integer type = 1;

  @Schema(description = "密码")
  @NotBlank
  private String password;

}
