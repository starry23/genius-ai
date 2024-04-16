package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/03/10:47
 * @Description:
 */
@Schema(description = "ItemPartitionVo")
@Data
public class ItemPartitionVo {
    /**
     * 知识库项目ID
     */
    @Schema(description = "分区ID")
    private Long partitionId;

    /**
     * 文件名称
     */
    @Schema(description = "分区名字")
    private String partitionName;

}
