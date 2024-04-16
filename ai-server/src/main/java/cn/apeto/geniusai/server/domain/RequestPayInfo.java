package cn.apeto.geniusai.server.domain;

import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/17 7:33 下午
 */
@Data
public class RequestPayInfo {

    private String qrCode;

    private String orderNo;

    private Object params;
}
