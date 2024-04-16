package cn.apeto.geniusai.server.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author wanmingyu
 * @create 2023/8/30 15:38
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CalculateRealInfo {

    /**
     * 实付
     */
    private BigDecimal realAmount;
    /**
     * 原价
     */
    private BigDecimal originalAmount;

    /**
     * 会员价
     */
    private BigDecimal memberAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

}
