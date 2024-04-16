package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;

/**
 * @author apeto
 * @create 2023/4/8 6:12 下午
 */
@Data
@Schema(description = "购买会员DTO")
public class PayMemberDTO {

    @NotNull
    @Schema(description = "商品ID")
    private Long productId;

    @NotNull
    @Schema(description = "支付类型 10:微信 20:支付宝")
    private Integer payType;

    @NotNull
    @Schema(description = "10 会员卡 20 流量包")
    private Integer type;

    @Schema(description = "MWEB:手机浏览器H5  NATIVE: 电脑端", example = "MWEB")
    private String tradeType;

}
