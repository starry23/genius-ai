package cn.apeto.geniusai.server.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/14 17:21
 */
@Schema(description = "权益")
@Data
public class RightConfigVo {

    @Schema(description = "权益描述")
    private String rightsDesc;
}
