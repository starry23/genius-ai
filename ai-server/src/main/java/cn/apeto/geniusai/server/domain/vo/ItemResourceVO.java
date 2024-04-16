package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/8/2 11:18
 */
@Schema(description = "ItemResourceVO")
@Data
public class ItemResourceVO {

    /**
     * 资源ID
     */
    @Schema(description = "资源ID")
    private Long itemResourceId;

    /**
     * 知识库项目ID
     */
    @Schema(description = "知识库项目ID")
    private Long itemId;

    /**
     * 文件名称
     */
    @Schema(description = "文件名称")
    private String fileName;

    /**
     * 文件名称
     */
    @Schema(description = "文件原名")
    private String originalName;

    /**
     * 对象存储地址
     */
    @Schema(description = "对象存储全量地址")
    private String fileFullUrl;

    /**
     * 总结
     */
    @Schema(description = "总结")
    private String summaryDesc;

    /**
     * q1
     */
    @Schema(description = "q1")
    private String q1;

    /**
     * q2
     */
    @Schema(description = "q2")
    private String q2;

}
