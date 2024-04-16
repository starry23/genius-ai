package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/4/29 11:43 上午
 */
@Schema(description = "订单管理列表请求类")
@Data
@EqualsAndHashCode(callSuper = true)
public class ManagerOrdersDTO extends ReqPage {

  @Schema(description = "用户ID")
  private Long userId;

}
