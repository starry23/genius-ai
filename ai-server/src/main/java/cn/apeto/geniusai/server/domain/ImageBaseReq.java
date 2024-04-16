package cn.apeto.geniusai.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author apeto
 * @create 2023/6/9 12:20
 */
@Schema(description = "ImageBaseReq")
@Data
public class ImageBaseReq {

    @Schema(description = "自定义参数")
    protected String state;

    @Schema(description = "回调地址, 为空时使用全局notifyHook")
    protected String notifyHook;

    @Schema(description = "在body 参数中增加 mode 属性 fast relax turbo")
    private String mode;


}
