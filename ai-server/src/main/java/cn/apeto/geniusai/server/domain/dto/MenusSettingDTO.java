package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * @author wanmingyu
 * @create 2023/9/18 15:03
 */
@Data
public class MenusSettingDTO {

    @Schema(description = "项目ID")
    private Long itemId;

    @Schema(description = "菜单详情")
    private List<MenusDetail> menusDetails;
}
