package cn.apeto.geniusai.server.domain.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author hsg
 * @since 2023-07-04 16:28:17
 */
@Schema(description = "批量生兑换码")
@Data
public class CardDTO {


    @Schema(description = "生成时间")
    private LocalDateTime expirationDate;

    @Schema(description = "生成数量")
    private  Integer num;

    @Schema(description = "每个兑换码的额度")
    private BigDecimal eachAmount;

}
