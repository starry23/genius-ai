package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/6/18 1:04 上午
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountLogsDTO extends ReqPage {

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String outsideCode;

    /**
     * 1: 充值 2: 失效回收 3: 退款 4: 消费
     */
    @Schema(description = "操作类型")
    private Integer logDescriptionType;

    /**
     * 类型 10: 转入 20:转出
     */
    @Schema(description = "类型 10: 转入 20:转出")
    private Integer directionType;

    @Schema(description = "账户ID")
    private Long accountId;
}
