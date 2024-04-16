package cn.apeto.geniusai.server.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 卡密兑换
 * </p>
 *
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
@EqualsAndHashCode(callSuper = true)
@TableName("card")
@Data
public class Card extends BaseEntity {
    private static final long serialVersionUID = 1L;
    @Schema(description = "批次号，用于标识该兑换码属于哪个批次。", example = "1")
    private String batch;

    @Schema(description = "已兑换的用户ID。如果尚未被兑换，则此值为空。", example = "12345")
    private Long redeemedUid;

    @Schema(description = "兑换码。", example = "ABCD-1234-EFGH-5678")
    private String code;

    @Schema(description = "待待兑换 0  已兑换 1 已过期 2  已作废 3'", example = "待待兑换 0  已兑换 1  已过期 2  已作废 3")
    private Integer status;

    @Schema(description = "兑换码过期时间。", example = "2022-12-31 23:59:59")
    private LocalDateTime expirationDate;

    @Schema(description = "兑换时间。如果尚未被兑换，则此值为空。", example = "2022-01-01 10:00:00")
    private LocalDateTime redemptionDate;

    @Schema(description = "兑换额度（金额）。如果尚未被使用，则为0.", example = "100.00")
    private BigDecimal amount;

}
