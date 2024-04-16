package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wanmingyu
 * @create 2024/1/2 7:36 下午
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OpenApiMchConfigListDTO extends ReqPage {

    /**
     * 商户号
     */
    private String mchId;
}
