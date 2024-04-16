package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/6/10 4:43 下午
 */
@Data
@Schema(description = "MjImageInfoVO")
public class MjImageInfoVO {

    /**
     * 文件ID
     */
    @Schema(description = "文件ID")
    private String fileId;
    /**
     * url
     */
    @Schema(description = "cosUrl")
    private String cosUrl;

    /**
     * 生成图片: IMAGINE,选中放大:UPSCALE,选中其中的一张图，生成四张相似的:VARIATION,重新生成:REROLL,图转prompt:DESCRIBE,多图混合:BLEND
     */
    @Schema(description = "生成图片: IMAGINE, 选中放大:UPSCALE 选中其中的一张图，生成四张相似的:VARIATION, 重新生成:REROLL, 图转prompt:DESCRIBE, 多图混合:BLEND")
    private String fileAction;

    /**
     * 文件状态: 未启动: NOT_START 已提交: SUBMITTED 执行中: IN_PROGRESS 失败: FAILURE 成功: SUCCES
     */
    @Schema(description = "文件状态: 未启动: NOT_START 已提交: SUBMITTED 执行中: IN_PROGRESS 失败: FAILURE 成功: SUCCES")
    private String fileStatus;

    /**
     * 提示词
     */
    @Schema(description = "提示词")
    private String filePrompt;

    /**
     * 进度
     */
    @Schema(description = "进度")
    private String progress;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "发布状态")
    private String publishState;

    @Schema(description = "按钮信息")
    private Object changeButtonInfo;
}
