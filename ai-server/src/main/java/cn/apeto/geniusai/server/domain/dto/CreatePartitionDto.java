package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/11:11
 * @Description:
 */
@Schema(description = "CreateItemDTO")
@Data
public class CreatePartitionDto {
    @NotNull
    @Schema(description = "项目ID")
    private Long itemId;

    private Long itemResourceId;

}
