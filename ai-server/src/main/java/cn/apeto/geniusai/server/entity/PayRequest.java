package cn.apeto.geniusai.server.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author apeto
 * @create 2023/7/28 17:17
 */
@Data
public class PayRequest {

    private long userId;
    private Integer type;
    private String orderNo;
    private String goodsCode;
    private String goodsName;
    private BigDecimal amount;
    private Integer payType;
    private String clientIP;
    private String tradeType;
}
