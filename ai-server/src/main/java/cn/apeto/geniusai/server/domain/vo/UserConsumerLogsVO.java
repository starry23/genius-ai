package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wanmingyu
 * @create 2023/8/30 16:38
 */
@Schema(description = "UserConsumerLogsVO")
@Data
public class UserConsumerLogsVO {

    /**
     * 商品类型
     */
    @Schema(description = "商品类型")
    private String productTypeDesc;

    /**
     * 原价
     */
    @Schema(description = "原价")
    private BigDecimal originalAmount;

    /**
     * 实付
     */
    @Schema(description = "实付")
    private BigDecimal realAmount;

    /**
     * 会员价
     */
    @Schema(description = "会员价")
    private BigDecimal memberAmount;

    /**
     * 优惠金额
     */
    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;
}
