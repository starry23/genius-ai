package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author wanmingyu
 * @create 2023/9/18 14:53
 */
@Schema(description = "WelcomeSettingDTO")
@Data
public class WelcomeSettingDTO {

    @Schema(description = "项目ID")
    private Long itemId;

    @Schema(description = "提示语")
    private String welcome;

    @Schema(description = "便捷提问")
    private List<String> fastPrompts;
}
