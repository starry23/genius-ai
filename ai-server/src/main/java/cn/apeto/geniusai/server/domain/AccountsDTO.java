package cn.apeto.geniusai.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/6/18 1:02 上午
 */
@Schema(description = "账户列表")
@EqualsAndHashCode(callSuper = true)
@Data
public class AccountsDTO extends ReqPage{

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "账户类型")
    private Integer accountType;
}
