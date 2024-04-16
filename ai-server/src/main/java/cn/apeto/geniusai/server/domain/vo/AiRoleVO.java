package cn.apeto.geniusai.server.domain.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/5/30 14:29
 */
@Schema(description = "角色信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiRoleVO {

    @Schema(description = "角色ID")
    private Long id;
    /**
     * 角色名称
     */
    @Schema(description = "角色名称")
    private String roleName;
    /**
     * 角色描述
     */
    @Schema(description = "角色描述")
    private String roleDesc;

    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * 图片URL
     */
    @Schema(description = "图片URL")
    private String imageUrl;
}
