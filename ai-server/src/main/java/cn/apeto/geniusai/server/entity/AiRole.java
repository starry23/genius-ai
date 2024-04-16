package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * AI角色
 * </p>
 *
 * @author warape
 * @since 2023-05-30 02:01:25
 */
@Schema(description = "AI角色")
@Getter
@Setter
@TableName("ai_role")
public class AiRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    @TableField("role_desc")
    private String roleDesc;

    /**
     * 1: {@link cn.apeto.geniusai.server.domain.Constants.RoleTypeEnum}
     */
    @Schema(description = "角色类型")
    @TableField("role_type")
    private Integer roleType;

    /**
     * 提示词
     */
    @Schema(description = "提示词")
    @TableField("prompt")
    private String prompt;
    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    @TableField("image_url")
    private String imageUrl;
    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    @TableField (exist = false)
    private String fullUrl;

}
