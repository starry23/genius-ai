package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * mj图片信息
 * </p>
 *
 * @author warape
 * @since 2023-06-10 04:01:04
 */
@Getter
@Setter
@TableName("mj_image_info")
public class MjImageInfo extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * mj_id
     */
    @TableField("mj_id")
    private String mjId;

    /**
     * url
     */
    @TableField("cos_url")
    private String cosUrl;

    /**
     * 生成图片: IMAGINE,选中放大:UPSCALE,选中其中的一张图，生成四张相似的:VARIATION,重新生成:REROLL,图转prompt:DESCRIBE,多图混合:BLEND
     */
    @TableField("file_action")
    private String fileAction;

    /**
     * 文件状态: 未启动: NOT_START 已提交: SUBMITTED 执行中: IN_PROGRESS 失败: FAILURE 成功: SUCCES
     */
    @TableField("file_status")
    private String fileStatus;

    /**
     * 文件发布状态: 未发布: NOT_PUBLISH 已发布: PUBLISHED
     */
    @TableField("publish_state")
    private String publishState;

    /**
     * 提示词
     */
    @TableField("file_prompt")
    private String filePrompt;

    /**
     * 进度
     */
    @TableField("progress")
    private String progress;

    @TableField("fail_reason")
    private String failReason;

    /**
     * 服务商类型
     */
    @TableField("service_type")
    private String serviceType;
    /**
     * 服务商类型
     */
    @TableField("change_button_info")
    private String changeButtonInfo;

    @TableField("product_type")
    private Integer productType;


}
