package cn.apeto.geniusai.server.domain.vo;

import cn.apeto.geniusai.server.domain.dto.MenusDetail;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author wanmingyu
 * @create 2023/9/18 15:15
 */
@Schema(description = "WelcomeAndMenusVO")
@Data
public class WelcomeAndMenusVO {

    @Schema(description = "提示语")
    private String welcome;

    @Schema(description = "便捷提问")
    private List<String> fastPrompts;

    @Schema(description = "菜单详情")
    private List<MenusDetail> menusDetails;
}
