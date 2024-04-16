package cn.apeto.geniusai.server.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author wanmingyu
 * @create 2023/10/23 14:42
 */
@Data
public class MemberExpConsumeVO {

    private BigDecimal amount;

    private Long accountId;
}
