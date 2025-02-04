package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "MemberExchange")
@Data
public class MemberExchangeDTO extends ReqPage {

    @Schema(description = "用户ID")
    private String userId;
}
