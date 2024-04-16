package cn.apeto.geniusai.server.domain.dto;

import cn.apeto.geniusai.server.domain.ReqPage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author apeto
 * @create 2023/6/24 4:38 下午
 */
@Schema(description = "MJManagerListDTO")
@EqualsAndHashCode(callSuper = true)
@Data
public class MJManagerListDTO extends ReqPage {

    @Schema(description = "用户ID")
    private Long userId;
}
