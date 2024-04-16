package cn.apeto.geniusai.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;


/**
 * @author apeto
 * @create 2023/6/17 7:26 下午
 */
@Schema(description = "DataPackage")
@Data
public class DataPackage {

    @NotNull
    @Schema(description = "流量包ID")
    private Long dataPackageId;

    @NotNull
    @Schema(description = "支付类型 10:微信 20:支付宝")
    private Integer payType;

}
