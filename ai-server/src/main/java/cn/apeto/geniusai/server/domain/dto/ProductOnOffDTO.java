package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/22 9:39 下午
 */
@Schema(description = "ProductOnOffDTO")
@Data
public class ProductOnOffDTO {

    @Schema(description = "id")
    private Long id;

    @Schema(description = "状态")
    private Integer status;
}
