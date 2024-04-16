package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author apeto
 * @create 2023/7/8 12:05 上午
 */
@Data
@Schema(description = "账户余额VO")
public class AccountBalanceVO {

    @Schema(description = "代币余额")
    private BigDecimal tokenBalance = BigDecimal.ZERO;
    @Schema(description = "人民币余额")
    private BigDecimal rmbBalance = BigDecimal.ZERO;
    @Schema(description = "MJ余额")
    private BigDecimal mjBalance = BigDecimal.ZERO;
    @Schema(description = "知识库上传余额")
    private BigDecimal knowledgeBalance = BigDecimal.ZERO;

}
