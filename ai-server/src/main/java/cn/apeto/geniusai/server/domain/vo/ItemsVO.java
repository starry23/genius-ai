package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/8/2 11:10
 */
@Schema(description = "项目VO")
@Data
public class ItemsVO {

    @Schema(description = "项目ID")
    private Long itemId;
    @Schema(description = "项目名称")
    private String itemName;
    @Schema(description = "项目描述")
    private String itemDesc;
    @Schema(description = "聊天reqId")
    private String reqId;
}
