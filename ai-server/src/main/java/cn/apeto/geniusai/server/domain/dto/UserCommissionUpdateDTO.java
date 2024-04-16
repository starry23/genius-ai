package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/30 9:49 上午
 */
@Data
public class UserCommissionUpdateDTO {

    @Schema(description = "UID")
    private Long userId;
    @Schema(description = "数量")
    private Integer num;
}
