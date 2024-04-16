package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author apeto
 * @create 2023/4/2 5:19 下午
 */
@Schema(description = "会话记录")
@Data
public class SessionRecordVo {

  @Schema(description = "请求ID")
  private String reqId;

  @Schema(description = "文案")
  private String content;

  @Schema(description = "角色")
  private String chatRole;

  @Schema(description = "角色ID")
  private Long roleId;

  @Schema(description = "创建时间")
  private Date createTime;

  @Schema(description = "产品类型")
  private Integer productType;

  @Schema(description = "会话名称")
  private String sessionName;
  @Schema(description = "模型Id")
  private String model;
  @Schema(description = "绘画详情")
  private String sessionDesc;
  @Schema(description = "图标")
  private String icon;
  @Schema(description = "文件ID")
  private Long fileId;
  @Schema(description = "文件名")
  private String fileName;



}
