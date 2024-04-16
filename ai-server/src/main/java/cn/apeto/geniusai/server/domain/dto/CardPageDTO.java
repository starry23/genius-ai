package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @author apeto
 * @create 2023/7/8 11:32 上午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CardPageDTO extends ReqPage {

    @Schema(description = "批次号，用于标识该兑换码属于哪个批次。", example = "1")
    private String batch;

    @Schema(description = "已兑换的用户ID。如果尚未被兑换，则此值为空。", example = "12345")
    private Long redeemedUid;

    @Schema(description = "兑换码。", example = "ABCD-1234-EFGH-5678")
    private String code;

    @Schema(description = "待待兑换 0  已兑换 1 已过期 2  已作废 3'", example = "待待兑换 0  已兑换 1  已过期 2  已作废 3")
    private Integer status;

    @Schema(description = "兑换时间。如果尚未被兑换，则此值为空。", example = "2022-01-01 10:00:00")
    private LocalDateTime redemptionDate;

}
