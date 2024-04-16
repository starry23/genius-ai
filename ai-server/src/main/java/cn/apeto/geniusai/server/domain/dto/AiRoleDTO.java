package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author apeto
 * @create 2023/5/30 14:29
 */
@EqualsAndHashCode(callSuper = true)
@Schema(description = "角色信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiRoleDTO extends ReqPage {

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;

    /**
     * 角色类型
     */
    @Schema(description = "角色类型")
    private Integer roleType;

}
