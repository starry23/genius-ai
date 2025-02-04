package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/4/29 11:51 上午
 */
@Schema(description = "提问日志")
@Data
@EqualsAndHashCode(callSuper = true)
public class QLogsDTO extends ReqPage {

  @Schema(description = "请求id")
  private String reqId;
  @Schema(description = "用户id")
  private Long userId;

}
