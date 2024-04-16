package cn.apeto.geniusai.server.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/28 14:13
 */
@Data
public class BingInfoDTO {

    @Schema(description = "账户号")
    private String bindAccountNum;

    @Schema(description = "绑定登录类型")
    private Integer loginType;
}
