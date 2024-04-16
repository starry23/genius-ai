package cn.apeto.geniusai.server.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/5/4 14:03
 */
@Schema(description = "充值会员卡")
@Data
public class DepositMemberDTO {

  @Schema(description = "用户ID")
  @NotNull
  private Long userId;
  @Schema(description = "会员卡编码")
  @NotBlank
  private String memberCode;

}
