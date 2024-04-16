package cn.apeto.geniusai.server.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author apeto
 * @create 2023/7/8 5:09 下午
 */
@Data
public class CommissionWithdrawDTO {

    private Long userId;

    private BigDecimal amount;
}
