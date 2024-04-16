package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/7/31 10:35 下午
 */
@Schema(description = "CreateItemDTO")
@Data
public class CreateItemDTO {

    @Schema(description = "项目名")
    private String itemName;
    @Schema(description = "项目描述")
    private String itemDesc;
}
