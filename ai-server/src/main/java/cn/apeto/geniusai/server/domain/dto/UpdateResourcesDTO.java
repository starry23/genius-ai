package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author apeto
 * @create 2023/7/31 10:51 下午
 */
@Schema(description = "UpdateResourcesDTO")
@Data
public class UpdateResourcesDTO {

    @Schema(description = "文件file")
    private MultipartFile file;
    @Schema(description = "知识库ID")
    private Long knowledgeItemId;

}
