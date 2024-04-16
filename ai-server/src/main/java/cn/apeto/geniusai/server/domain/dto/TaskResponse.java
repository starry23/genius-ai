package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/12 16:27
 */
@Data
@Schema(description = "TaskResponse")
public class TaskResponse {

    private String action;
    @Schema(description = "任务ID")
    private String id;
    @Schema(description = "提示词")
    private String prompt;
    @Schema(description = "提示词-英文")
    private String promptEn;
    @Schema(description = "任务描述")
    private String description;
    @Schema(description = "自定义参数")
    private String state;
    @Schema(description = "提交时间")
    private Long submitTime;
    @Schema(description = "开始执行时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long finishTime;
    @Schema(description = "图片url")
    private String imageUrl;
    @Schema(description = "cos图片url")
    private String cosImageUrl;
    @Schema(description = "任务状态")
    private String status ;
    @Schema(description = "任务进度")
    private String progress;
    @Schema(description = "失败原因")
    private String failReason;

}
