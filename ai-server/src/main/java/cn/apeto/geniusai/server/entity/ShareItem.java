package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author:
 * @Date: 2023/08/05/11:10
 * @Description:
 */
@Getter
@Setter
@TableName("share_item")
public class ShareItem extends BaseEntity{

    @Schema(description = "uuid")
    @TableField("uuid")
    private String uuid;

    @Schema(description = "userId")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "项目ID")
    @TableField("item_id")
    private Long itemId;

    @Schema(description = "是否开启分享 1是 0否")
    @TableField("is_enable")
    private Integer isEnable;

    @Schema(description = "分享类型 10: web",defaultValue = "10")
    @TableField("share_type")
    private Integer shareType;
}
