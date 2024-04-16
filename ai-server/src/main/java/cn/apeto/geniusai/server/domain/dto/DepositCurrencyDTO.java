package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author apeto
 * @create 2023/6/18 1:56 上午
 */
@Data
public class DepositCurrencyDTO {

    @Schema(description = "用户ID")
    @NotNull
    private Long userId;
    @Schema(description = "数量")
    @NotNull
    private Integer count;
    @NotNull
    @Schema(description = "账户类型")
    private Integer accountType;
}
